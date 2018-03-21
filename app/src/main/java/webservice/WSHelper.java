package webservice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import database.Adapter;
import sv.edu.uca.dei.fies2018v2.R;

/**
 * Created by Varela on 09/03/2018.
 */

public class WSHelper implements Runnable{
    ProgressDialog progress;
    static final String TAG = "WSHelper";
    URL url;
    private String ws_url;
    private String line;
    private Context context;


    //para base de datos.
    private Adapter adapter;

    public WSHelper(Context context){
        adapter = new Adapter(context);
        ws_url  = "http://dei.uca.edu.sv/webservice/fies/webService.php";
        //inicializando url
        try {
            url = new URL(ws_url);
        } catch (MalformedURLException e) {
            Log.e(TAG +": url", e.toString());
        }
        progress = ProgressDialog.show(context, "",
                "Por favor espere, sincronizando datos con el servidor...", true);
        this.context = context;
    }

    @Override
    public void run() {
        String[] tableList = {"equipo","accesorio"};

        for (int i = 0; i<tableList.length ;i++){
            //abriendo conexion
            HttpURLConnection httpCon = getHttpCon();
            boolean connectionRes = false;

            //obteniendo tabla en json
            adapter.open();
            String json = adapter.tableToJSON(tableList[i], tableList[i]);
            adapter.close();

            if(httpCon != null && json.length() > 2){
                //asignando datos al hashmap
                HashMap<String, String> postDataParams = new HashMap<>();
                postDataParams.put("tabla", tableList[i]);
                postDataParams.put("jsondata", json);

                //enviando datos a webservice
                try{
                    connectionRes = HttpWrite(httpCon, jsonFromHashMap(postDataParams));
                }
                catch (IOException e) {
                    Log.w(TAG+": runWrite", e.toString());
                }
                if(connectionRes){
                    //recibiendo respuesta de webservice
                    String serverResponse = HttpRead(httpCon);
                    Log.d(TAG, serverResponse);
                    udpateDataBase(serverResponse);
                }
            }
            else{
                Log.i(TAG +"thread for","fallo conexion o tabla vacia");
            }
            //cerrando conexion
            closeHttpCon(httpCon);
        }
        progress.dismiss();
    }

    private String HttpRead(HttpURLConnection httpCon){
        String response = "";
        InputStream inputStream = null;
        try {
            int res=httpCon.getResponseCode();
            if (res == HttpsURLConnection.HTTP_OK) {
                inputStream = httpCon.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line=bufferedReader.readLine()) != null) {
                    response+=line;
                }

                bufferedReader.close();
            }
        } catch (IOException e) {
            Log.w(TAG+": read", e.toString());
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
            Log.w(TAG+": write", e.toString());
            /*showMsg(context, "Ocurrió un error al intentar conectarse al centro de datos, " +
                    "por favor verifique que el servidor este en linea y " +
                    "que su dispositivo disponga de una conexión a Internet", 2);*/
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
            Log.w(TAG+" getCon", e.toString());
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
        //Log.d(TAG, result.toString());
        return result.toString();
    }

    private void udpateDataBase(String jsonString){
        if(jsonString.length() > 2){
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject myJsonObject = (JSONObject) jsonArray.get(i);

                    Log.d(TAG, "tabla: "+myJsonObject.get("table")+", id: "+ myJsonObject.get("id")+", global: "+ myJsonObject.get("global"));
                }
            } catch (JSONException e) {
                Log.w(TAG, e.toString());
            }
        }
    }





}
