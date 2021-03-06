package webservice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

import Objetos.Usuario;
import database.Adapter;
import sv.edu.uca.dei.fies2018v2.R;

public class LoadData extends AsyncTask<Void, Void, String>{

    private static final String TAG = "LoadData";
    private Context context;
    private ProgressDialog pDialog;

    private final String ws_url;
    URL url;
    private String line;
    private String response = "";

    private Adapter adapter;

    //constructor
    public LoadData(Context context){
        this.context = context;
        adapter = new Adapter(context);
        //deifiniendo conexion
        ws_url  = "http://dei.uca.edu.sv/webservice/fies/webService.php";
        try {
            url = new URL(ws_url);
        } catch (MalformedURLException e) {
            Log.e(TAG +": url", e.toString());
        }
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        pDialog = new ProgressDialog(context) ;
        pDialog.setMessage("Cargando informacion. por favor espere...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected String doInBackground(Void... params) {
        //verificando si el dispositivo tiene internet
        if(!isInternetWorking("https://google.com")){
            return "4";
        }
        //verificando si el webservice es accesible
        if(!isServerReachable(context,ws_url)){
            return "5";
        }

        //String[] tableList = {"equipo","accesorio","mxe", "medicion", "variable", "subvariable"};
        //String[] globalColumnList = {"idEquipoGlobal","idAccesorioGlobal","idMxeGlobal", "idMedicionGlobal", "idVariableGlobal", "idSubVariableGlobal"};

        /*String[] tableList = {"equipo",
                "accesorio",
                "mxe",
                "medicion",
                "variable",
                "subvariable",
                "equipoAnalizado",
                "medicionelec",
                "segelectrica",
                "sxe"};
        String[] globalColumnList = {"idEquipoGlobal",
                "idAccesorioGlobal",
                "idMxeGlobal",
                "idMedicionGlobal",
                "idVariableGlobal",
                "idSubVariableGlobal",
                "idEquipoAnalizadoGlobal",
                "medicionElecGlobal",
                "idSegElectricaGlobal",
                "idsxeGlobal"};
        */

        String[] tableList = {
                "equipo",
                "accesorio",
                "medicion",
                "variable",
                "subvariable",
                "mxe",
                "equipoAnalizado",
                "segelectrica",
                "medicionelec",
                "sxe",
        };
        String[] globalColumnList = {
                "idEquipoGlobal",
                "idAccesorioGlobal",
                "idMedicionGlobal",
                "idVariableGlobal",
                "idSubVariableGlobal",
                "idMxeGlobal",
                "idEquipoAnalizadoGlobal",
                "idSegElectricaGlobal",
                "medicionElecGlobal",
                "idsxeGlobal"
        };


        //si son las tablas variable o subvariable se debe verificar si hay global que asignar antes de subir
        adapter.open();
        adapter.check_global_id_variable();
        adapter.check_global_id_subvariable();
        adapter.close();

        int JSONNULLCont = 0;
        for (int i = 0; i<tableList.length ;i++){
            //abriendo conexion
            HttpURLConnection httpCon = getHttpCon();

            if(httpCon != null){

                //obteniendo tabla en json
                adapter.open();
                String json = adapter.tableToJSON(tableList[i], globalColumnList[i]);
                adapter.close();

                if(json != null){
                    Log.e(TAG+", json: ", json);
                    //asignando datos al hashmap
                    HashMap<String, String> postDataParams = new HashMap<>();
                    postDataParams.put("tabla", tableList[i]);
                    postDataParams.put("jsondata", json);

                    //enviando datos a webservice
                    boolean connectionRes = false;
                    try{
                        connectionRes = HttpWrite(httpCon, jsonFromHashMap(postDataParams));
                    }
                    catch (IOException e) {
                        Log.w(TAG+": runWrite", e.toString());
                    }
                    if(connectionRes){
                        //recibiendo respuesta de webservice
                        String serverResponse = HttpRead(httpCon);
                        Log.e(TAG+", server: ", serverResponse);
                        if (serverResponse != null)
                            udpateDataBase(serverResponse);
                    }else{
                        return "3";
                    }
                }
                else{
                    JSONNULLCont++;
                }
            }
            else{
                Log.i(TAG +"thread for","fallo conexion");
                return "1";
            }
            //cerrando conexion
            closeHttpCon(httpCon);
        }
        if(JSONNULLCont==10) //10 es porque se actualizan 10 tablas
            return "2";
        else
            return null;
    }

    /*
    * ERRORES:
    * 1-Fallo al inicializar HttpURLConnection
    * 2-tabla vacia o no hay nada que actualizar
    * 3-error al intentar enviar o recibir datos.
    * 4-El dispositivo no tiene internet
    * 5-El webservice es inaccesible
    * */
    @Override
    protected void onPostExecute(String result) {
        if(result != null){
            Log.d(TAG+"onPost","Codigo resultante: "+result);
            if(result == "4")
                showMsg(context, "Ocurrió un error al intentar sincronizar: El dispositivo no dispone de una conexión a internet. Verifique su conexión y vuelva a intentarlo.", 2);
            else if(result == "3")
                showMsg(context, "Ocurrió un error al intentar enviar o recibir datos.", 2);
            else if(result == "2")
                showMsg(context, "Actualmente los datos del dispositivo y del servidor estan sincronizados. Por lo que se omitirá este proceso.", 0);
            else if(result == "5")
                showMsg(context, "Ocurrió un error al intentar sincronizar: El sitio web que alojará los datos no responde, por favor intentelo mas tarde y notifique a algun administrador.", 2);
            else
                showMsg(context, "Ocurrió un error al intentar sincronizar", 2);
        }
        else{
            //Log.d(TAG+"onPost","Result is null, todo bien");
            showMsg(context, "¡ok! La información ha sido sincronizada correctamente", 1);
        }
        pDialog.dismiss();
    }


    private String HttpRead(HttpURLConnection httpCon){
        String response = null;
        InputStream inputStream = null;
        try {
            int res=httpCon.getResponseCode();
            if (res == HttpsURLConnection.HTTP_OK) {
                response = "";
                inputStream = httpCon.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line=bufferedReader.readLine()) != null) {
                    response+=line;
                }

                bufferedReader.close();
            }
        } catch (IOException e) {
            Log.e(TAG+": read", e.toString());
        }
        return response;
    }

    private boolean HttpWrite(HttpURLConnection httpCon, String json){
        boolean result = false;
        OutputStream outPutStream = null;
        try {
            //abriendo flujo
            outPutStream = httpCon.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outPutStream, "UTF-8"));
            //escribiendo
            bufferedWriter.write(json);
            //cerrando flujo
            bufferedWriter.flush();
            bufferedWriter.close();
            outPutStream.close();
            return true;
        }
        catch (IOException e) {
            Log.e(TAG+": write", e.toString());
            return false;
        }
    }
    private HttpURLConnection getHttpCon(){
        HttpURLConnection httpCon = null;
        try {
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setReadTimeout(20000);
            httpCon.setConnectTimeout(20000);
            httpCon.setRequestMethod("POST");
            httpCon.setDoInput(true);
            httpCon.setDoOutput(true);
        } catch (IOException e) {
            Log.e(TAG+" getCon", e.toString());
        }
        return httpCon;
    }

    private void closeHttpCon(HttpURLConnection httpCon){
        httpCon.disconnect();
    }

    private String jsonFromHashMap(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        Log.d(TAG, result.toString());
        return result.toString();
    }

    private void udpateDataBase(String jsonString){
        if(jsonString.length() > 2){
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject myJsonObject = (JSONObject) jsonArray.get(i);
                    Log.d(TAG, "tabla: "+myJsonObject.get("table")+", id: "+ myJsonObject.get("id")+", global: "+ myJsonObject.get("global"));
                    String tableName = myJsonObject.get("table").toString();
                    String idData = myJsonObject.get("id").toString();
                    String idGlobalData = myJsonObject.get("global").toString();

                    //Log.d(TAG, "tabla:"+tableName);
                    String idColumn = null;
                    String idGlobalColumn = null;
                    if (tableName.equals("equipo")){
                        idColumn = "idEquipo";
                        idGlobalColumn = "idEquipoGlobal";
                    }
                    else if (tableName.equals("accesorio")){
                        idColumn = "idAccesorio";
                        idGlobalColumn = "idAccesorioGlobal";
                    }
                    else if (tableName.equals("mxe")){
                        idColumn = "idmxe";
                        idGlobalColumn = "idMxeGlobal";
                    }
                    else if (tableName.equals("medicion")){
                        idColumn = "idMedicion";
                        idGlobalColumn = "idMedicionGlobal";
                    }
                    else if (tableName.equals("variable")){
                        idColumn = "idVariable";
                        idGlobalColumn = "idVariableGlobal";
                    }
                    else if (tableName.equals("subvariable")){
                        idColumn = "idSubVariable";
                        idGlobalColumn = "idSubVariableGlobal";
                    }
                    else if (tableName.equals("equipoAnalizado")){
                        idColumn = "idEquipoAnalizado";
                        idGlobalColumn = "idEquipoAnalizadoGlobal";
                    }
                    else if (tableName.equals("medicionelec")){
                        idColumn = "id";
                        idGlobalColumn = "medicionElecGlobal";
                    }
                    else if (tableName.equals("segelectrica")){
                        idColumn = "idSegElectrica";
                        idGlobalColumn = "idSegElectricaGlobal";
                    }
                    else if (tableName.equals("sxe")){
                        idColumn = "idsxe";
                        idGlobalColumn = "idsxeGlobal";
                    }


                    adapter.open();
                    // public boolean updateGlobalId(String tabla, String idColumnName, String idValue, String globalColumnName, String globalValue) {
                    adapter.updateGlobalId(tableName, idColumn, idData, idGlobalColumn, idGlobalData);
                    adapter.close();

                    //actualizando dependencias de tabla medicion (para webservice: tablas variable y mxe)
                    /*
                    dependencias
                        medicion > variable
                        medicion > mxe
                        variable > subvariable
                        equipo > accesorio
                        equipoAnalizado > segelectrica
                        segelectrica > medicionelec
                        segelectrica > sxe
                    */
                    adapter.open();
                    if (tableName.equals("medicion")){
                        adapter.update_global_dependencies("variable","idMedicion", idData, "idMedicionGlobal", idGlobalData);
                        adapter.update_global_dependencies("mxe","idMedicion", idData, "idMedicionGlobal", idGlobalData);
                    }
                    //actualizando dependencias de tabla variable (para webservice: tabla subvariable)
                    if (tableName.equals("variable")){
                        adapter.update_global_dependencies("subvariable","idVariable", idData, "idVariableGlobal", idGlobalData);
                    }
                    //actualizando dependencias de tabla equipo (para webservice: tabla accesorio)
                    if (tableName.equals("equipo")){
                        adapter.update_global_dependencies("accesorio","idEquipo", idData, "idEquipoGlobal", idGlobalData);
                    }
                    //actualizando dependencias de tabla equipoAnalizado (para webservice: tabla segelectrica)
                    if (tableName.equals("equipoAnalizado")){
                        adapter.update_global_dependencies("segelectrica","idEquipoAnalizado", idData, "idEquipoAnalizadoGlobal", idGlobalData);
                    }
                    //actualizando dependencias de tabla segelectrica  (para webservice: tabla medicionelec y sxe)
                    if (tableName.equals("segelectrica")){
                        adapter.update_global_dependencies("medicionelec","id_segelectrica", idData, "idSegElectricaGlobal", idGlobalData);
                        adapter.update_global_dependencies("sxe","idSegElectrica", idData, "idSegElectricaGlobal", idGlobalData);
                    }
                    adapter.close();

                    Log.e(TAG+" update", "UPDATE "+tableName+" SET "+idGlobalColumn+" = "+idGlobalData+" WHERE "+idColumn+" = "+idData);
                }
            } catch (JSONException e) {
                Log.w(TAG + "udpateDataBase", e.toString());
            }
        }
    }

    private void showMsg(Context context, String msg, int tipoMensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        if(tipoMensaje == 0){
            builder.setTitle("¡Advertencia!")
                    .setIcon(R.drawable.ic_warning);
        }
        else if(tipoMensaje == 1){
            builder.setTitle("¡Ok!")
                    .setIcon(R.drawable.ic_success);
        }
        else if(tipoMensaje == 2){
            builder.setTitle("¡Error!")
                    .setIcon(R.drawable.ic_error);
        }
        else{
            builder.setTitle("???")
                    .setIcon(R.drawable.ic_warning);
        }
        AlertDialog alert = builder.create();
        alert.show();//showing the dialog
    }

    public boolean isInternetWorking(String site) {
        boolean success = false;
        try {
            URL url = new URL(site);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean isServerReachable(Context context, String srvUrl) {
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(srvUrl);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                urlConn.connect();
                if (urlConn.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
}

