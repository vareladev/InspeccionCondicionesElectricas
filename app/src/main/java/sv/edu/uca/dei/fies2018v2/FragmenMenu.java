package sv.edu.uca.dei.fies2018v2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import database.Adapter;

import static android.content.ContentValues.TAG;


public class FragmenMenu extends Fragment {

    //database
    private Adapter adapter;

    //aux for export to xls
    private static SQLiteToExcel sqliteToExcel;
    private static File downloadDir;



    public FragmenMenu() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragmen_menu, container, false);
        LinearLayout btnEquipo1 = (LinearLayout) view.findViewById(R.id.Menu_boton1); //nuevo registro
        LinearLayout btnEquipo3 = (LinearLayout) view.findViewById(R.id.Menu_boton3); //history
        LinearLayout btnEquipo2 = (LinearLayout) view.findViewById(R.id.Menu_boton2); //equipo
        LinearLayout Menu_boton4 = (LinearLayout) view.findViewById(R.id.Menu_boton4); //sincronizar
        LinearLayout Menu_boton5 = (LinearLayout) view.findViewById(R.id.Menu_boton5); //exportar



        btnEquipo2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((Home)getActivity()).openFragmentEquipo();
            }
        });
        btnEquipo3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((Home)getActivity()).openFragmentHistory();
                //((Home)getActivity()).openVarRegMed();
            }
        });
        btnEquipo1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((Home)getActivity()).openFragNewMed();
                //((Home)getActivity()).openVarRegMed();
            }
        });

        Menu_boton4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
        Menu_boton5.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogExport();
            }
        });


        return view;
    }

    private void dialogExport(){
        //calculando nombre de archivo
        DateFormat df = new SimpleDateFormat("HHmmss");
        String date = df.format(Calendar.getInstance().getTime());

        //Base de datos
        adapter = new Adapter(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_export,null);
        builder.setView(dialogView);

        //objetos del popup
        Button btn_export_close = (Button)dialogView.findViewById(R.id.btn_export_close);
        Button btn_export = (Button)dialogView.findViewById(R.id.btn_export);
        final EditText txt_get_filename = (EditText)dialogView.findViewById(R.id.txt_get_filename);
        txt_get_filename.setText("FIES_Export_"+date);

        final AlertDialog dialog = builder.create();

        //Listener de los botones guardar y cancelar
        btn_export_close .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //dialogChkSegElec();
            }
        });
        btn_export .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(checkEditTextIsEmpty(txt_get_filename)){
                downloadDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                ArrayList<String> tableList = new ArrayList<String>();
                tableList.add("accesorio");
                tableList.add("equipo");
                tableList.add("medicion");
                tableList.add("mxe");
                tableList.add("variable");
                tableList.add("subvariable");

                sqliteToExcel = new SQLiteToExcel(getActivity(), "fies2018.db",downloadDir.toString()+"/");
                sqliteToExcel.exportSpecificTables(tableList, txt_get_filename.getText().toString()+
                        ".xls", new SQLiteToExcel.ExportListener() {

                    ProgressDialog progressDialog;

                    @Override
                    public void onStart() {
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Exportando, por favor espere ...");
                        progressDialog.show();
                    }

                    @Override
                    public void onCompleted(String filePath) {
                        //Toast.makeText(getActivity(), "Successfully exported database", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        dialog.dismiss();
                        showMsg(getActivity(), "la base de datos ha sido exportada con éxito, el archivo fue guardado en la carpeta \"descargas\" del dispositivo. ",1);
                    }

                    @Override
                    public void onError(Exception e) {
                        //Toast.makeText(getActivity(), "Error in exporting database: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.getStackTrace()[0].toString());
                        progressDialog.dismiss();
                        dialog.dismiss();
                        showMsg(getActivity(), "Ocurrió un error al intentar exportar el archivo. ",2);
                    }
                });
            }
            }
        });
        dialog.show();
    }

    //*************************************************
    //DEFINIR CAMPOS OBLIGATORIOS
    public static boolean checkEditTextIsEmpty(EditText... editTexts){
        try{
            for (EditText editText : editTexts){
                if (editText.getText().toString().trim().length() == 0){
                    editText.requestFocus();
                    editText.setError("Este campo no puede quedar vacio");
                    return false;
                }
            }
        }
        catch (Exception ignored){
            return false;
        }
        return true;
    }

    //*************************************************
    //MOSTRAR MENSAJE
    public void showMsg(Context context, String msg, int tipoMensaje){
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
        AlertDialog alert = builder.create();
        alert.show();//showing the dialog
    }
}
