package database;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Adapter {
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public Adapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public Adapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public Adapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }


    //CONSULTAS A BASE DE DATOS
    //SQL login
    public boolean checkUser(String dui, String pass) {
        boolean foundUser = false;
        String sql ="select * from usuario where dui = '"+dui+"' and pass ='"+pass+"';";
        try{
            Cursor c = mDb.rawQuery(sql, null);
            if(c.moveToFirst()){
                foundUser = true;
            }
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "checkUser >>"+ mSQLException.toString());
            throw mSQLException;
        }
        finally {
            return foundUser;
        }
    }

    //obtener equipo version light
    public String[][] getEquipoPreview(){
        String sql ="select marca, modelo, serie, numeroInventario, fechaCalibracion, idEquipo from equipo;";
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "getEquipoPreview >>>>>"+ mSQLException.toString());
            throw mSQLException;
        }
        finally {
            ArrayList<ArrayList<String>> HMequipo = new ArrayList<ArrayList<String>>();
            ArrayList<String> mArrayList = new ArrayList<String>();
            if (c.moveToFirst()) {
                do {
                    int columnsQty = c.getColumnCount();
                    for (int idx=0; idx<columnsQty; ++idx) {
                        mArrayList.add(c.getString(idx));
                    }
                    //Log.v(TAG, String.format("Row: %d, Values: %s", c.getPosition(),mArrayList.toString()));
                    HMequipo.add(mArrayList);
                    mArrayList = new ArrayList<String>();
                } while (c.moveToNext());
            }
            //Log.v(TAG, String.format("Values: %s", HMequipo.toString()));
            String[][] arrayEquipo = new String[HMequipo.size()][];
            String[] blankArray = new String[0];
            for(int i=0; i < HMequipo.size(); i++) {
                arrayEquipo[i] = HMequipo.get(i).toArray(blankArray);
            }
            return arrayEquipo;
        }
    }

    //Insertar registro de equipo
    public boolean newEquip(String marca, String modelo, String serie, String fecha, int correlInventario){
        boolean result = false;
        ContentValues values = new ContentValues();
        //String sql = "insert into equipo (codigoActivo, ubicacion, nota) VALUES ('"+codigoActivo+"', '"+ubicacion+"', '"+nota+"');";
        try{
            //mDb.execSQL(sql);
            values.put("marca", marca);
            values.put("modelo", modelo);
            values.put("serie", serie);
            values.put("fechaCalibracion", fecha);
            values.put("numeroInventario", correlInventario);

            mDb.insert("equipo", null, values);
            result = true;
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "Adapter>newEquip >>"+ mSQLException.toString());
            //throw mSQLException;
        }
        finally {
            return result;
        }
    }

    //obtener detalle de equipo
    public ArrayList<String> getEquipoDetail(int idEquipo){
        String sql ="select marca, modelo, serie, numeroInventario, fechaCalibracion from equipo where idEquipo ="+idEquipo+";";
        ArrayList<String> mArrayList = new ArrayList<String>();
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! getEquipoPreview >>>>>"+ mSQLException.toString());
        }
        finally {

            if (c.moveToFirst()) {
                    int columnsQty = c.getColumnCount();
                    for (int idx=0; idx<columnsQty; ++idx) {
                        mArrayList.add(c.getString(idx));
                    }
            }
            return mArrayList;
        }
    }

    //actualizar tabla equipo
    public boolean updateEquipo(String[] row) {
        int result = 0;
        ContentValues args = new ContentValues();
        try {
            args.put("marca", row[1]);
            args.put("modelo", row[2]);
            args.put("serie", row[3]);
            args.put("numeroInventario", row[4]);
            args.put("fechaCalibracion", row[5]);
            result = mDb.update("equipo", args, "idEquipo" + "=" + row[0], null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! updateEquipo >>>>>"+ mSQLException.toString());
        }
        finally{
            return result > 0;
        }
    }

    //eliminar equipo
    public boolean deteleteEquip(int idEquipo) {
        int result = 0;
        try {
            result = mDb.delete("equipo", "idEquipo" + "=" + idEquipo, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! DelEquipo >>>>>"+ mSQLException.toString());
        }
        finally{
            return result > 0;
        }
    }

    //*************************************************************************
    // CONSULTAS TABLA ACCESORIO
    //*************************************************************************
    public boolean newAccessory(String[] accesorios, int idEquipo){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            for (int i=0; i< accesorios.length ; i++){
                values.put("accesorio", accesorios[i]);
                values.put("idEquipo", idEquipo);
                mDb.insert("accesorio", null, values);
            }
            result = true;
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "Adapter>newEquip >>"+ mSQLException.toString());
        }
        return result;
    }

    //obtener accesorios
    public  List<String> getTools(int idEquipo){
        String sql ="select accesorio from accesorio where idEquipo="+idEquipo+";";
        List<String> toolsValues = new ArrayList<String>();
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "getTools >>>>>"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                do {
                    toolsValues.add(c.getString(0));
                } while (c.moveToNext());
            }
            return toolsValues;
        }
    }
    public boolean delAllTools(int idEquipo) {
        int result = 0;
        try {
            result = mDb.delete("accesorio", "idEquipo" + "=" + idEquipo, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! DelEquipo >>>>>"+ mSQLException.toString());
        }
        finally{
            return result > 0;
        }
    }
    //*************************************************************************
    // CONSULTAS GENERALES
    //*************************************************************************
    //ultimo correlativo de alguna tabla
    public int getLastId(String tabla){
        int idTable = 0;
        String sql ="select seq from sqlite_sequence where name='"+tabla+"';";
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "getEquipoPreview >>>>>"+ mSQLException.toString());
            throw mSQLException;
        }
        finally {
            if (c.moveToFirst()) {
                idTable = c.getInt(0);
            }
            return idTable;
        }
    }
/*
    public Cursor getTestData()
    {
        try
        {
            String sql ="SELECT * FROM activo";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null)
            {
                mCur.moveToNext();
            }
            return mCur;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }



    //SQL login
    public boolean checkUser(String user, String pass) {
        boolean foundUser = false;
        String sql ="select * from user where username = '"+user+"' and pass ='"+pass+"';";
        try{
            Cursor c = mDb.rawQuery(sql, null);
            if(c.moveToFirst()){
                foundUser = true;
            }
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "checkUser >>"+ mSQLException.toString());
            throw mSQLException;
        }
        finally {
            return foundUser;
        }
    }


    //SQL ADDREGISTER ACTIVITY
    public Cursor getInfo(String codigo){
        String sql ="select * from activo where codigo='"+codigo+"';";
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "checkUser >>"+ mSQLException.toString());
            throw mSQLException;
        }
        finally {
            return c;
        }
    }

    public boolean saveInfo(String codigoActivo, String ubicacion, String nota){
        boolean result = false;
        ContentValues values = new ContentValues();
        String sql = "insert into inventario (codigoActivo, ubicacion, nota) VALUES ('"+codigoActivo+"', '"+ubicacion+"', '"+nota+"');";

        try{
            mDb.execSQL(sql);
            values.put("codigo", codigoActivo);
            values.put("nombre", ubicacion);
            values.put("ubicacion", nota);

            mDb.insert("inventario", null, values);
            result = true;
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "saveInfo >>"+ mSQLException.toString());
            throw mSQLException;
        }
        finally {
            return result;
        }
    }

    //activity show info query
    public List getCodes(){
        List<String> list = null;

        String sql ="select codigo from activo;";
        try{
            Cursor c = mDb.rawQuery(sql, null);
            list = new ArrayList<String>();
            list.add("Seleccione codigo...");
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        list.add(c.getString(0));
                    } while (c.moveToNext());
                }
            }

        }
        catch (SQLException mSQLException){
            Log.e(TAG, "checkUser >>"+ mSQLException.toString());
            throw mSQLException;
        }
        finally {
            return list;
        }

    }

    public List<RegistroInventario> getInventory(String codigo){
        List<RegistroInventario> list = null;
        RegistroInventario registroInventario = null;

        //para fecha
        String dateString;

        String sql ="select fecha, ubicacion, nota from inventario where codigoActivo='"+codigo+"';";

        try{
            Cursor c = mDb.rawQuery(sql, null);
            list = new ArrayList<RegistroInventario>();
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        dateString = c.getString(0);
                        Log.i("REGISTRO >>>>>>>>>>",dateString);
                        registroInventario = new RegistroInventario(formatDateTime(dateString), c.getString(1), c.getString(2));
                        list.add(registroInventario);
                    } while (c.moveToNext());
                }
            }

        }
        catch (SQLException mSQLException){
            Log.e(TAG, "checkUser >>"+ mSQLException.toString());
            throw mSQLException;
        }
        finally {
            return list;
        }

    }

    public String formatDateTime(String fecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d=new Date();
        try {
            d=  dateFormat.parse(fecha);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        switch(month){
            case 1: fecha = "Enero ";
                break;
            case 2: fecha = "Febrero ";
                break;
            case 3: fecha = "Marzo ";
                break;
            case 4: fecha = "Abril ";
                break;
            case 5: fecha = "Mayo ";
                break;
            case 6: fecha = "Junio ";
                break;
            case 7: fecha = "Julio ";
                break;
            case 8: fecha = "Agosto ";
                break;
            case 9: fecha = "Septiembre ";
                break;
            case 10: fecha = "Octubre ";
                break;
            case 11: fecha = "Noviembre ";
                break;
            case 12: fecha = "Septiembre ";
                break;
        }

        fecha = fecha + day + ", " + year;
        return fecha;
    }
*/
}
