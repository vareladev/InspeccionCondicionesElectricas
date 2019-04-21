package sv.edu.uca.dei.fies2018v2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import Objetos.Equipo;
import Objetos.Usuario;
import database.Adapter;
import webservice.LoadData;
import webservice.WSHelper;

import static android.content.ContentValues.TAG;
import static sv.edu.uca.dei.fies2018v2.MenuMediciones.getSavedObjectFromPreference;


public class FragmenMenu extends Fragment {
    //++++++++++++++++++++++++++++++++++
    //Database adapter
    private Adapter adapter;

    public static Equipo EquipFromList_1 = null; //para obtener un equipo de la lista de seleccion
    public static Equipo EquipFromList_2 = null;

    static boolean isNextEnabled = false;
    //++++++++++++++++++++++++++++++++++

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
        //++++++++++++++++++++++++++++++++++
        //usuario conectado:
        final Usuario currentUser = getSavedObjectFromPreference(getActivity(), "preferences", "currentUser", Usuario.class);

        //Base de datos
        adapter = new Adapter(getActivity());
        adapter.createDatabase();

        //botones de menu de este fragmento
        LinearLayout btnEquipo1 = (LinearLayout) view.findViewById(R.id.Menu_boton1); //Levanta PopUp Seleccion de equipo
        LinearLayout Menu_boton6 = (LinearLayout) view.findViewById(R.id.Menu_boton6); //Seguridad electrica
        //++++++++++++++++++++++++++++++++++
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
        //++++++++++++++++++++++++++++++++++
        /*btnEquipo1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((Home)getActivity()).openFragNewMed();
            }
        });*/
        btnEquipo1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogOpenSelectEquip(1);
            }
        });
        //++++++++++++++++++++++++++++++++++

        Menu_boton4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //new Thread(new WSHelper(getActivity())).start();
                //Thread wsHelper = new Thread(new WSHelper(getActivity()));
                //WSHelper wsHelper = new WSHelper(getActivity());
                //wsHelper.run();
                new LoadData(getActivity()).execute();

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

        //abriendo fragmento de inpeccion de seguridad electrica
        Menu_boton6.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //((Home)getActivity()).openFragSegElec();
                dialogOpenSelectEquip(2);
            }
        });

        return view;
    }

    //++++++++++++++++++++++++++++++++++
    //1 - medicion de condiciones ambientales e inspeccion de receptaculos
    //2 - Medicion de seguridad electrica
    private void dialogOpenSelectEquip(final int tipoMedicion){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_select_equipment_1,null);
        builder.setView(dialogView);

        //Objetos de PopUp
        final ListView listSelectEquip_1 = (ListView)dialogView.findViewById(R.id.ListSelectEquip_1);
        final ListView listSelectEquip_2 = (ListView)dialogView.findViewById(R.id.ListSelectEquip_2);
        final Button btnAddEquip_1 = (Button)dialogView.findViewById(R.id.btnAddEquip_1);
        final Button btnRetEquip_1 = (Button)dialogView.findViewById(R.id.btnRetEquip_1);
        final Button btn_create_measure = (Button)dialogView.findViewById(R.id.btnCreateMeasure);

        //Lista 1
        //obteniendo lista de equipos desde la base de datos:
        adapter.open();
        final ArrayList<Equipo> listaEquipo_1 = adapter.getEquipsForList();
        adapter.close();
        //creando adaptador
        final ArrayAdapter<Equipo> adapter_1 = new ArrayAdapter<Equipo>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listaEquipo_1);
        //seteando adaptador
        listSelectEquip_1.setAdapter(adapter_1);
        //definiendo color de background
        listSelectEquip_1.setBackgroundColor(getResources().getColor(R.color.table_even));
        //Listener de la lista 1
        listSelectEquip_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < listSelectEquip_1.getChildCount(); i++) {
                    if(position == i ){
                        EquipFromList_1 = (Equipo) listaEquipo_1.get(position);
                        //Toast.makeText(getContext(), "equipo ID: "+ultimoEquipSeleccionado.getId()+",  equipo Name : "+ultimoEquipSeleccionado.getEquipo(), Toast.LENGTH_SHORT).show();
                        listSelectEquip_1.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.divider));
                    }else{
                        listSelectEquip_1.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });

        //lista 2: lista de equipos a utilizar.
        //creando adaptador
        final ArrayList<Equipo> listaEquipo_2 = new ArrayList<>();
        final ArrayAdapter<Equipo> adapter_2 = new ArrayAdapter<Equipo>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listaEquipo_2);
        //seteando adaptador
        listSelectEquip_2.setAdapter(adapter_2);
        //Listener de la lista 2
        listSelectEquip_2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < listSelectEquip_2.getChildCount(); i++) {
                    if(position == i ){
                        EquipFromList_2 = (Equipo) listaEquipo_2.get(position);
                        listSelectEquip_2.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.divider));
                    }else{
                        listSelectEquip_2.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });

        //validando si se habilita el boton o no.
        if(listSelectEquip_2.getAdapter().getCount() == 0){
            btn_create_measure.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_disabled_button));
            isNextEnabled = false;
        }

        //creando ventana
        final AlertDialog dialog = builder.create();


        //Listener boton de añadir equipo a la medicion
        btnAddEquip_1 .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EquipFromList_1 != null){
                    //coloreando todas las posiciones del listview
                    for (int i = 0; i < listSelectEquip_1.getChildCount(); i++)
                        listSelectEquip_1.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    //removiendo de lista de equipos de la base de datos, el elemento selccionado
                    adapter_1.remove(EquipFromList_1);
                    adapter_1.notifyDataSetChanged();
                    //agregando a la lista RIGHT el elemento seleccionado
                    adapter_2.add(EquipFromList_1);
                    adapter_2.notifyDataSetChanged();
                    //volviendo a variable auxiliar equipo nulo
                    EquipFromList_1 = null;
                    //habilitando boton de iniciar una nueva medicion.
                    if(!isNextEnabled){
                        btn_create_measure.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_round_corners));
                        isNextEnabled = true;
                    }
                }
            }
        });

        //listener boton de eliminar equipo de la medicion
        btnRetEquip_1 .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EquipFromList_2 != null){
                    //coloreando todas las posiciones del listview
                    for (int i = 0; i < listSelectEquip_2.getChildCount(); i++)
                        listSelectEquip_2.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    //removiendo de lista RIGHT el elemento selccionado
                    adapter_2.remove(EquipFromList_2);
                    adapter_2.notifyDataSetChanged();
                    //agregando a la lista LEFT el elemento seleccionado
                    adapter_1.add(EquipFromList_2);
                    adapter_1.notifyDataSetChanged();
                    //volviendo a variable auxiliar equipo nulo
                    EquipFromList_2 = null;
                    //desabilitando boton si se queda sin elementos la lista
                    if(listSelectEquip_2.getAdapter().getCount() == 0){
                        btn_create_measure.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_disabled_button));
                        isNextEnabled = false;
                    }
                }
            }
        });

        btn_create_measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNextEnabled){
                    isNextEnabled = false;
                    //recorriendo items de la lista dos y guardardolas en un arraylist
                    ArrayList<String> EquipsForNextPage = new ArrayList<>();
                    Equipo e = null;
                    for (int i = 0; i < listSelectEquip_2.getChildCount(); i++){
                        e = (Equipo)listSelectEquip_2.getAdapter().getItem(i);
                        EquipsForNextPage.add(e.getId()+"");
                    }
                    //eliminando la ventana
                    dialog.dismiss();

                    if(tipoMedicion == 1){
                        //accediendo al fragmento de definicion de nueva medición
                        ((Home)getActivity()).openFragNewMed(EquipsForNextPage);
                    }
                    else if(tipoMedicion == 2){
                        //accediendo al fragmento de definicion de nueva medición de seguridad electrica
                        ((Home)getActivity()).openFragSegElec(EquipsForNextPage);
                    }

                }
            }
        });


        //mostrando dialogo
        dialog.show();
    }

    //++++++++++++++++++++++++++++++++++

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
                tableList.add("equipoAnalizado");
                tableList.add("segelectrica");
                tableList.add("medicionelec");

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
