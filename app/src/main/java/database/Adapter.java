package database;


import java.io.IOException;
import java.lang.reflect.Array;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Objetos.*;

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
    public Usuario checkUser(String dui, String pass) {
        String sql ="select idUsuario, dui, nombre from usuario where dui = '"+dui+"' and pass ='"+pass+"';";
        Usuario usuario = null;
        try{
            Cursor c = mDb.rawQuery(sql, null);
            if(c.moveToFirst()){
                usuario = new Usuario(c.getInt(0)+"",c.getString(1),c.getString(2));
            }
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "checkUser >>"+ mSQLException.toString());
            throw mSQLException;
        }
        finally {
            return usuario;
        }
    }

    //*************************************************************************
    // CONSULTAS TABLA EQUIPO
    //*************************************************************************
    //obtener equipo version light
    public String[][] getEquipoPreview(){
        String sql ="select nombre, marca, modelo, numeroInventario, fechaCalibracion, idEquipo from equipo;";
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
    public boolean newEquip(String nombre,String marca, String modelo, String serie, String fecha, int correlInventario){
        boolean result = false;
        ContentValues values = new ContentValues();
        //String sql = "insert into equipo (codigoActivo, ubicacion, nota) VALUES ('"+codigoActivo+"', '"+ubicacion+"', '"+nota+"');";
        try{
            //mDb.execSQL(sql);
            values.put("nombre", nombre);
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
        String sql ="select marca, modelo, serie, numeroInventario, fechaCalibracion, nombre from equipo where idEquipo ="+idEquipo+";";
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

    //obtener lista simple de equipo para pantalla de creacion de nueva medicion
    public Equipo getEquipForNewMea(String idEquipo){
        String sql ="select idEquipo, modelo, nombre from equipo where idEquipo="+idEquipo+";";
        Log.e(TAG, "SQL getEquipForNewMea >>>>>"+ sql);
        Equipo e = null;
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! getEquipoPreview >>>>>"+ mSQLException.toString());
        }
        finally {

            if (c.moveToFirst()) {
                e = new Equipo(c.getInt(0)+"", c.getString(1)+" - "+c.getString(2));
            }
            return e;
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
            args.put("nombre", row[6]);
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
    //obtener lista de equipos para listview de seleccion de equipo para inicio de medicion
    public ArrayList<Equipo> getEquipsForList(){
        String sql ="select idEquipo, modelo, nombre from equipo;";

        ArrayList<Equipo> EquipList = new ArrayList<>();
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! getEquipsForList >>>>>"+ mSQLException.toString());
        }
        finally {

            if (c.moveToFirst()) {
                do {
                    EquipList.add(new Equipo(c.getInt(0)+"", c.getString(1)+" - "+c.getString(2)));
                } while (c.moveToNext());
            }
            return EquipList;
        }
    }

    //obtener detalle de equipo
    public ArrayList<Equipo> getEquipForEdit(int idMedicion, boolean equals){
        String sql;

        if (equals)
            sql ="select mxe.idEquipo, equipo.marca from mxe,equipo where mxe.idEquipo = equipo.idEquipo and mxe.idMedicion == "+idMedicion+";";
        else
            sql ="select mxe.idEquipo, equipo.marca from mxe,equipo where mxe.idEquipo = equipo.idEquipo and mxe.idMedicion <> "+idMedicion+";";
            //sql ="select mxe.idEquipo, equipo.marca from mxe,equipo where mxe.idEquipo = equipo.idEquipo and mxe.idMedicion = "+idMedicion+";";

        ArrayList<Equipo> EquipList = new ArrayList<>();
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! getEquipForEdit: "+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                do {
                    EquipList.add(new Equipo(c.getInt(0)+"", c.getString(1)));
                } while (c.moveToNext());
            }
            return EquipList;
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
    // CONSULTAS ENTIDAD HOSPITAL
    //*************************************************************************
    //obtener detalle de equipo
    public ArrayList<Hospital> getHospitals(){
        String sql ="select idHospital, nombre from hospital;";

        ArrayList<Hospital> hospitalList = new ArrayList<>();
        //ArrayList<String> mArrayList = new ArrayList<String>();
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! getHospitals >>>>>"+ mSQLException.toString());
        }
        finally {

            if (c.moveToFirst()) {
                do {
                    hospitalList.add(new Hospital(c.getInt(0)+"", c.getString(1)));
                } while (c.moveToNext());
            }
            return hospitalList;
        }
    }

    public String getHospitalName (int idHospital){
        final String sql ="select nombre from hospital where idHospital = "+idHospital+";";
        String hospitalName = null;
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "getHospitalName >"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                hospitalName = c.getString(0);
            }
            return hospitalName;
        }
    }

    //*************************************************************************
    // CONSULTAS TABLA AREA
    //*************************************************************************
    //obtener lista de areas
    public ArrayList<Area> getAreas(String idHospital){
        String sql ="select idArea, nombreArea, plano, idHospital from area Where idHospital = '"+idHospital+"';";
        ArrayList<Area> AreasList = new ArrayList<>();
        byte[] byteArray;
        Bitmap plano;
        //ArrayList<String> mArrayList = new ArrayList<String>();
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! getAreas >>>>>"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                do {
                    //obteniendo blob
                    byteArray = c.getBlob(2);
                    //convirtiendo a bitmap
                    plano = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                    //agregando area a arreglo
                    AreasList.add(new Area(c.getInt(0)+"", c.getString(1),plano,c.getInt(3)+""));
                } while (c.moveToNext());
            }
            return AreasList;
        }
    }

    public Area getArea (int idArea){
        final String sql ="select nombreArea, plano from area where idArea = "+idArea+";";
        Cursor c = null;
        Area area = null;
        byte[] byteArray;
        Bitmap plano;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "getAreaName >"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                //convirtiendo a bitmap
                byteArray = c.getBlob(1);
                plano = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                area = new Area(null, c.getString(0),plano,null);
            }
            return area;
        }
    }

    //*************************************************************************
    // CONSULTAS TABLA MEDICION
    //*************************************************************************
    //crear nuevo registro
    public boolean mewMeasurement(NuevaMedicion nuevaMedicion){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            values.put("servicioAnalizado", nuevaMedicion.getServicioAnalizado());
            values.put("responsable", nuevaMedicion.getResponsable());
            values.put("telefono", nuevaMedicion.getTelefono());
            values.put("idArea", nuevaMedicion.getIdArea());
            values.put("idUsuario", nuevaMedicion.getIdUsuario());
            mDb.insert("medicion", null, values);
            result = true;
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "Adapter>mewMeasurement >>"+ mSQLException.toString());
        }
        return result;
    }

    //crear nuevo registro
    public NuevaMedicion getMeasure(int idMedicion){
        final String sql ="select servicioAnalizado, responsable, fecha from medicion where idMedicion = "+idMedicion+";";
        Cursor c = null;
        NuevaMedicion nuevaMedicion = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "getMeasure >"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                //convirtiendo a bitmap
                nuevaMedicion = new NuevaMedicion(null, null, c.getString(0), c.getString(1), null, c.getString(2));
            }
            return nuevaMedicion;
        }
    }

    //registrar nuevo comentario
    public boolean updateMeaComment(int idMedicion, String comentario){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            values.put("comentario", comentario);
            mDb.update("medicion", values, "idMedicion" + "=" + idMedicion, null);
            result = true;
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "Adapter>updateMeaComment >>"+ mSQLException.toString());
        }
        return result;
    }

    public String getMeaComment(int idMedicion){
        final String sql ="select comentario from medicion where idMedicion = "+idMedicion+";";
        Cursor c = null;
        String comentario = "";
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "getMeasure >"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                comentario =  c.getString(0);
            }
            return comentario;
        }
    }
    //*************************************************************************
    // CONSULTAS TABLA MXE
    //crear nuevo registro
    public boolean newMxeReg(int idMedicion, Equipo e){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            values.put("idMedicion", idMedicion);
            values.put("idEquipo", e.getId());
            mDb.insert("mxe", null, values);
            result = true;
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "Adapter>newMxeReg >>"+ mSQLException.toString());
        }
        return result;
    }


    //*************************************************************************
    // CONSULTAS TABLA VARIABLE
    //crear nuevo registro
    public boolean newVariable(int idMedicion, float valor ,int tipo, int isok){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            values.put("idMedicion", idMedicion);
            if(tipo != 1){ //medicion electrica
                values.put("valor", valor);
                values.put("cumple", isok);
            }
            values.put("tipo", tipo);
            mDb.insert("variable", null, values);
            result = true;
            Log.v("Adapter>newVariable >>", "Registro de variable Exitoso!!");
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "Adapter>newVariable >>"+ mSQLException.toString());
        }
        return result;
    }
    //contar numero de receptaculos electricos
    public int countReceptacles(int idMedicion){
        String sql ="select count(*) from variable Where idMedicion = "+idMedicion+" and tipo = 1;";
        Cursor c = null;
        int countReceptacles = 0;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! countReceptacles >>>>>"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                countReceptacles = c.getInt(0);
            }
            return countReceptacles;
        }
    }
    //contar numero de variables registradas (iluminacion, ruido, temp, humedad)
    public int countVariable(int idMedicion, int tipo){
        String sql ="select count(*) from variable Where idMedicion="+idMedicion+" and tipo="+tipo+";";
        Log.e(TAG, "countVariable >"+ sql);
        Cursor c = null;
        int countReceptacles = 0;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! countReceptacles >>>>>"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                countReceptacles = c.getInt(0);
                Log.e(TAG, "countVariable >"+ countReceptacles);
            }
            return countReceptacles;
        }
    }



    public  ArrayList<String[]> getVariableList(int idMedicion, int tipo){
        String[] variableRow;
        ArrayList<String[]> variableList = new ArrayList<String[]>();
        String sql = "select valor, cumple from variable where idMedicion = "+idMedicion+" and tipo ="+tipo+";";
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR-getVariableList"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                int contador = 0;
                do {
                    contador ++;
                    variableRow = new String[3];
                    variableRow[0] = contador+"";
                    variableRow[1] = c.getFloat(0)+"";
                    variableRow[2] = c.getInt(1) == 1 ? "Si" : "No";
                    variableList.add(variableRow);
                } while (c.moveToNext());
            }
            return variableList;
        }
    }
    public boolean newVariableCon(int idMedicion, float valor ,int tipo, int isok){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            values.put("idMedicion", idMedicion);
            values.put("valor", valor);
            values.put("cumple", isok);
            values.put("tipo", tipo);
            mDb.insert("variable", null, values);
            result = true;
            Log.v("Adapter>newVariable >>", "Registro de variable Exitoso!!");
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "Adapter>newVariableCon"+ mSQLException.toString());
        }
        return result;
    }
    //*************************************************************************
    // CONSULTAS TABLA SUBVARIABLE
    //crear nuevo registro
    public boolean newSubVariable(int idVariable, float polaridad, float vfaseneutro ,float vneutrotierra, float vfasetierra){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            values.put("idVariable", idVariable);
            values.put("polaridad", polaridad);
            values.put("vfaseneutro", vfaseneutro);
            values.put("vneutrotierra", vneutrotierra);
            values.put("vfasetierra", vfasetierra);
            mDb.insert("subvariable", null, values);
            result = true;
            Log.v("Adptr>newSubVariable:", "Registro de variable Exitoso!!");
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "Adapter>newSubVariable >>"+ mSQLException.toString());
        }
        return result;
    }
    public ArrayList<String[]> getSubVariableList(int idMedicion){
        String[] subVariableRow;
        ArrayList<String[]> subVariableList = new ArrayList<String[]>();
        String sql2 = "select subvariable.polaridad, subvariable.vfaseneutro, subvariable.vneutrotierra, subvariable.vfasetierra , subvariable.idSubVariable" +
                " from medicion, variable, subvariable" +
                " where medicion.idMedicion = variable.idMedicion" +
                " and variable.idVariable = subvariable.idVariable" +
                " and variable.tipo = 1" +
                " and medicion.idMedicion = "+idMedicion+
                " order by  subvariable.idSubVariable asc;";
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql2, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR-getSubVariableList"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                int contador = 0;
                do {
                    contador ++;
                    subVariableRow = new String[5];
                    subVariableRow[0] = contador+"";
                    subVariableRow[1] = c.getInt(0) == 1 ? "Si" : "No";
                    subVariableRow[2] = c.getFloat(1)+"";
                    subVariableRow[3] = c.getFloat(2)+"";
                    subVariableRow[4] = c.getFloat(3)+"";
                    subVariableList.add(subVariableRow);
                } while (c.moveToNext());
            }
            return subVariableList;
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
            Log.e(TAG, "getLastId:"+ mSQLException.toString());
            throw mSQLException;
        }
        finally {
            if (c.moveToFirst()) {
                idTable = c.getInt(0);
            }
            return idTable;
        }
    }

    public ArrayList<String[]> getMeasuresList(){
        String[] measureRow;
        ArrayList<String[]> measureList = new ArrayList<String[]>();
        String sql2 = "select hospital.seudo, area.nombreArea, medicion.fecha, medicion.idMedicion, hospital.idHospital, area.idArea " +
                "from hospital, area, medicion " +
                "where hospital.idHospital = area.idHospital and area.idArea = medicion.idArea;";
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql2, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR-getMeasuresList"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                do {
                    measureRow = new String[7];
                    measureRow[0] = c.getString(0);
                    measureRow[1] = c.getString(1);
                    measureRow[2] = c.getString(2);
                    measureRow[3] = "No";
                    measureRow[4] = c.getInt(3)+"";
                    measureRow[5] = c.getInt(4)+"";
                    measureRow[6] = c.getInt(5)+"";

                    measureList.add(measureRow);
                } while (c.moveToNext());
            }
            return measureList;
        }
    }


    //*************************************************************************
    // Tabla equipoAnalizado
    //*************************************************************************
    //crear nuevo registro
    public boolean insertNewEquiAna(String nombre, String marca, String modelo, String ns, String ninv, int idClase,int idTipo){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            values.put("nombre", nombre);
            values.put("marca", marca);
            values.put("modelo", modelo);
            values.put("ns", ns);
            values.put("ninventario", ninv);
            values.put("idClase", idClase);
            values.put("idTipo", idTipo);
            mDb.insert("equipoAnalizado", null, values);
            result = true;
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "insertNewEquiAna: "+ mSQLException.toString());
        }
        return result;
    }

    public boolean insertNewElecSec(int idHospital, String servicio, String responsable, int equipoAnalizado){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            values.put("idHospital", idHospital);
            values.put("servicioAnalizado", servicio);
            values.put("responsable", responsable);
            values.put("idEquipoAnalizado", equipoAnalizado);
            mDb.insert("segelectrica", null, values);
            result = true;
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "segelectrica: "+ mSQLException.toString());
        }
        return result;
    }

    public boolean InsertIntosxe(int idMedicion, int idEquipo){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            values.put("idSegElectrica", idMedicion);
            values.put("idEquipo", idEquipo);
            mDb.insert("sxe", null, values);
            result = true;
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "InsertIntosxe:"+ mSQLException.toString());
        }
        return result;
    }
    //*************************************************************************
    // Tabla tipo y clase (para clasificar un equipo medico
    //*************************************************************************
    public ArrayList<Clase> getClass4Equip(){
        String sql ="select * from clase;";
        ArrayList<Clase> classList = null;
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! getClass4Equip >>>>>"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                classList = new ArrayList<Clase>();
                do {
                    classList.add(new Clase(c.getInt(0),c.getString(1)));
                } while (c.moveToNext());
            }
        }
        return classList;
    }

    public ArrayList<Tipo> getType4Equip(){
        String sql ="select * from tipo;";
        ArrayList<Tipo> tipoList = null;
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! getType4Equip >>>>>"+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                tipoList = new ArrayList<Tipo>();
                do {
                    tipoList.add(new Tipo(c.getInt(0),c.getString(1)));
                } while (c.moveToNext());
            }
        }
        return tipoList;
    }

    //*************************************************************************
    // Tabla temp_medicionelec
    //*************************************************************************
    //agregando nuevo registro
    public boolean insertNewTempMed(int id_segelectrica, int id_bloque, int id_pregunta, float medicion, float estandar, String magnitud, String comentario){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            values.put("id_segelectrica", id_segelectrica);
            values.put("id_bloque", id_bloque);
            values.put("id_pregunta", id_pregunta);
            values.put("medicion", medicion);
            values.put("estandar", estandar);
            values.put("magnitud", magnitud);
            values.put("comentario", comentario);
            mDb.insert("temp_medicionelec", null, values);
            result = true;
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "insertNewTempMed: "+ mSQLException.toString());
        }
        return result;
    }

    //Elimininar registro temporal segun bloque
    public int deleteTempMed(int id_bloque) {
        int result = 0;
        try {
            result = mDb.delete("temp_medicionelec", "id_bloque" + "=" + id_bloque, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! deleteTempMed: "+ mSQLException.toString());
        }
        finally{
            return result;
        }
    }

    //borrar toda la tabla.
    public void deleteAllTempMed() {
        try {
            mDb.delete("temp_medicionelec", null, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! deleteAllTempMed: "+ mSQLException.toString());
        }
    }

    //obtener todos los datos de la tabla temporal de mediciones de seguridad electrica
    public ArrayList<NuevaMedicionSegElec> getAllTempMed() {
        String sql ="select id_segelectrica, id_bloque, id_pregunta, medicion,estandar, magnitud, comentario from temp_medicionelec;";
        ArrayList<NuevaMedicionSegElec> tempData = null;
        Cursor c = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! getAllTempMed: "+ mSQLException.toString());
        }
        finally {
            if (c.moveToFirst()) {
                tempData = new ArrayList<NuevaMedicionSegElec>();
                do {
                    //tempData.add(new Tipo(c.getInt(0),c.getString(1)));
                    tempData.add(new NuevaMedicionSegElec(
                        c.getInt(0),
                            c.getInt(1),
                            c.getInt(2),
                            c.getFloat(3),
                            c.getFloat(4),
                            c.getString(5),
                            c.getString(6)
                    ));
                } while (c.moveToNext());
            }
        }
        return tempData;
    }

    //agregando nuevo registro
    public boolean insertNewSegElec(NuevaMedicionSegElec e){
        boolean result = false;
        ContentValues values = new ContentValues();
        try{
            values.put("id_segelectrica", e.getId_segelectrica());
            values.put("id_bloque", e.getId_bloque());
            values.put("id_pregunta", e.getId_pregunta());
            values.put("medicion", e.getMedicion());
            values.put("estandar", e.getEstandar());
            values.put("magnitud", e.getMagnitud());
            values.put("comentario", e.getComentario());
            mDb.insert("medicionelec", null, values);
            result = true;
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "insertNewTempMed: "+ mSQLException.toString());
        }
        return result;
    }
    //*************************************************************************
    // CONSULTAS JSON
    //*************************************************************************
    public JSONArray cur2Json(Cursor cursor) {
        JSONArray resultSet = null;
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            if(resultSet == null)
                resultSet = new JSONArray();
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;

    }

    public JSONObject cursorToJson(Cursor c) {
        JSONObject retVal = new JSONObject();
        for(int i=0; i<c.getColumnCount(); i++) {
            String cName = c.getColumnName(i);
            try {
                switch (c.getType(i)) {
                    case Cursor.FIELD_TYPE_INTEGER:
                        retVal.put(cName, c.getInt(i));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        retVal.put(cName, c.getFloat(i));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        retVal.put(cName, c.getString(i));
                        break;
                }
            }
            catch(Exception ex) {
                Log.e(TAG, "Exception converting cursor column to json field: " + cName + ", reason: " +ex.toString());
            }
        }
        return retVal;
    }

    //equipo to json
    public String tableToJSON(String table, String idColumnGlobal)  {
        JSONArray resultSet = new JSONArray();
        String sql ="select * from "+table+" where "+idColumnGlobal+" IS NULL  OR "+idColumnGlobal+" = '';";
        Cursor c = null;
        String row = null;
        try{
            c = mDb.rawQuery(sql, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! equipoToJson: "+ mSQLException.toString());
        }
        finally {
            JSONArray retVal =  cur2Json(c);
            if(retVal != null) {
                Log.w(TAG + ": RET: ", retVal.toString());
                return retVal.toString();
            }
            else
                return null;
        }
    }

    //actualizar tabla equipo
    public boolean updateGlobalId(String tabla, String idColumnName, String idValue, String globalColumnName, String globalValue) {
        int result = 0;
        ContentValues args = new ContentValues();
        try {
            args.put(globalColumnName, globalValue);
            result = mDb.update(tabla, args, idColumnName + "=" + idValue, null);
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "ERROR! updateGlobalId>"+ mSQLException.toString());
        }
        finally{
            return result > 0;
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
