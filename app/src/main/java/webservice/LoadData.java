package webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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

public class LoadData extends AsyncTask<Void, Void, String>{

    static final String TAG = "LoadData";
    private Context context;
    private ProgressDialog pDialog;

    private final String ws_url  = "http://dei.uca.edu.sv/webservice/fies/webService.php";
    private String line;
    private String response = "";

    private Adapter adapter;

    public LoadData(Context context){
        this.context = context;
        adapter = new Adapter(context);
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
        adapter.open();
        //Log.d(TAG, adapter.equipoToJson());
        String json = adapter.equipoToJson();
        adapter.close();
        try{
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("tabla", "equipo");
            postDataParams.put("jsonequipo", json);
            URL url;

            url = new URL(ws_url);

            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setReadTimeout(20000);
            httpCon.setConnectTimeout(20000);
            httpCon.setRequestMethod("POST");
            httpCon.setDoInput(true);
            httpCon.setDoOutput(true);

            OutputStream outPutStream = httpCon.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outPutStream, "UTF-8"));
            bufferedWriter.write(jsonFromHashMap(postDataParams));

            bufferedWriter.flush();
            bufferedWriter.close();
            outPutStream.close();

            int res=httpCon.getResponseCode();

            if (res == HttpsURLConnection.HTTP_OK) {
                BufferedReader br=new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }

                br.close();
            }
            else {
                response="";
            }
            Log.d(TAG, response);
            pDialog.dismiss();
            return response;

        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

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


}
