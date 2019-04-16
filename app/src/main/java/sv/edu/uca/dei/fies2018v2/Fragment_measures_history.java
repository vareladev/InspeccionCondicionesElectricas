package sv.edu.uca.dei.fies2018v2;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import database.Adapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import simpletable.SimpleTableHeaderAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_measures_history extends Fragment {
    //variables auxiliares
    private static int idMedicion;
    private String fechaMedicion;

    //Database adapter
    private Adapter adapter;

    //para tablas
    private static final String[] TABLE_HEADERS_ELEC = { "R", "Polaridad", "VfaseN", "VNeutroT", "VfaseT" };
    private static ArrayList<String[]> listReceptaclesId;
    private static SimpleTableDataAdapter tableAdapterElec;
    private static final String[] TABLE_HEADERS_VAR = { "Punto", "Valor","Cumple" };
    private static ArrayList<String[]> listConditions;
    private static SimpleTableDataAdapter tblAdapConditions;

    public Fragment_measures_history() {
        // Required empty public constructor
    }

    //para recibir parametro
    public static Fragment_measures_history newInstance(int idMedicion, String fechaMedicion) {
        Bundle bundle = new Bundle();
        bundle.putInt("idMedicion", idMedicion);
        bundle.putString("fechaMedicion", fechaMedicion);
        Fragment_measures_history fragment = new Fragment_measures_history();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            idMedicion = bundle.getInt("idMedicion");
            fechaMedicion = bundle.getString("fechaMedicion");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measures_history, container, false);
        //inicializacion de adaptador de base de datos
        adapter = new Adapter(getActivity());
        //leyendo parametros de entrada
        readBundle(getArguments());

        //objetos
        TextView measure_his_title = (TextView) view.findViewById(R.id.measure_his_title);
        LinearLayout detComment = (LinearLayout) view.findViewById(R.id.detComment);
        LinearLayout detHumData = (LinearLayout) view.findViewById(R.id.detHumData);
        LinearLayout detTempData = (LinearLayout) view.findViewById(R.id.detTempData);
        LinearLayout detNoiseData = (LinearLayout) view.findViewById(R.id.detNoiseData);
        LinearLayout detLuxData = (LinearLayout) view.findViewById(R.id.detLuxData);
        LinearLayout detElecData = (LinearLayout) view.findViewById(R.id.detElecData);


        //asignando datos
        measure_his_title.setText("Registro de medidas: inspección "+fechaMedicion.substring(0,10));

        //listeners
        detElecData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogChkSegElec();
            }
        });
        detLuxData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogCheckMedL(idMedicion, 2, "Condiciones ambientales: Iluminación", "Nuevo Registro: Iluminación");
            }
        });
        detNoiseData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogCheckMedL(idMedicion, 4, "Condiciones ambientales: Ruido", "Nuevo Registro: Ruido");
            }
        });
        detTempData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogCheckMedL(idMedicion, 5, "Condiciones ambientales: Temperatura", "Nuevo Registro: Temperatura");
            }
        });
        detHumData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogCheckMedL(idMedicion, 6, "Condiciones ambientales: Humedad", "Nuevo Registro: Humedad");
            }
        });
        detComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogGetComment(idMedicion);
            }
        });
        return view;
    }

    //*************************************************
    //administrar mediciones electricas
    private void dialogChkSegElec(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_chk_med_elec,null);
        builder.setView(dialogView);

        //inicializacion de objetos
        //Inicializacion de la tabla
        TableView<String[]> tableViewElec = (TableView<String[]>) dialogView.findViewById(R.id.tvSegElec);
        Button mBtnCloseChkElec = (Button)dialogView.findViewById(R.id.btnCloseChkElec);
        Button mBtnNewChkElec = (Button)dialogView.findViewById(R.id.btnNewChkElec);

        //cargar header
        tableViewElec.setHeaderAdapter(new SimpleTableHeaderAdapter(getActivity(), TABLE_HEADERS_ELEC));
        //cargar datos
        adapter.open();
        listReceptaclesId = adapter.getSubVariableList(idMedicion);
        adapter.close();
        //inicializar adaptador
        tableAdapterElec = new SimpleTableDataAdapter(getActivity(), listReceptaclesId);
        tableViewElec.setDataAdapter(tableAdapterElec);
        //cargar funcionalidad de click
        //tableViewElec.addDataClickListener(new AdministrarEquipo.EquipClickListener());
        //personalizacion de la tabla:
        int colorEvenRows = getResources().getColor(R.color.table_even);
        int colorOddRows = getResources().getColor(R.color.table_odd);
        tableViewElec.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));


        //objetos del popup
        //Button mBtnCancelSegEle = (Button)dialogView.findViewById(R.id.btnCancelMedElec);
        //Button mBtnGetMedElec = (Button)dialogView.findViewById(R.id.btnGetMedElec);

        final AlertDialog dialog = builder.create();

        //Listener de los botones guardar y cancelar
        mBtnCloseChkElec .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mBtnNewChkElec .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNewMedElec();
                //dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void dialogNewMedElec(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_med_electrica,null);
        builder.setView(dialogView);

        //objetos del popup
        final TextView mTxtvTitleMedElec = (TextView)dialogView.findViewById(R.id.txtvTitleMedElec);
        Button MBtnCancelMedElec = (Button)dialogView.findViewById(R.id.btnCancelMedElec);
        Button mBtnGetMedElec = (Button)dialogView.findViewById(R.id.btnGetMedElec);
        final CheckBox checkBoxPolaridad = (CheckBox)dialogView.findViewById(R.id.checkBoxPolaridad);
        //final EditText mTxtPolaridad = (EditText)dialogView.findViewById(R.id.txtPolaridad);
        final EditText mTxtVFaseNeutro = (EditText)dialogView.findViewById(R.id.txtVFaseNeutro);
        final EditText mTxtVNeutroTierra = (EditText)dialogView.findViewById(R.id.txtVNeutroTierra);
        final EditText mTxtVFaseTierra = (EditText)dialogView.findViewById(R.id.txtVFaseTierra);


        adapter.open();
        mTxtvTitleMedElec.setText("Recéptaculo R"+(adapter.countReceptacles(idMedicion)+1));
        adapter.close();

        final AlertDialog dialog = builder.create();
        //Listener de los botones guardar y cancelar
        MBtnCancelMedElec .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //dialogChkSegElec();
            }
        });
        mBtnGetMedElec .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEditTextIsEmpty(mTxtVFaseNeutro,mTxtVNeutroTierra,mTxtVFaseTierra )){
                    adapter.open();
                    //insertando la variable
                    if(adapter.newVariable(idMedicion,0, 1,0)){
                        //al ser med electrica se llena la tabla subvariable
                        int idVariable = adapter.getLastId("variable");
                        //float polaridad = Float.parseFloat(mTxtPolaridad.getText().toString());
                        int polaridad = 0;
                        if (checkBoxPolaridad.isChecked())
                            polaridad = 1;
                        float vfaseneutro = Float.parseFloat(mTxtVFaseNeutro.getText().toString());
                        float vneutrotierra = Float.parseFloat(mTxtVNeutroTierra.getText().toString());
                        float vfasetierra = Float.parseFloat(mTxtVFaseTierra.getText().toString());
                        if(adapter.newSubVariable(idVariable,polaridad,vfaseneutro,vneutrotierra,vfasetierra)){
                            String cadPolaridad = polaridad == 1 ? "Si" : "No";
                            String[] subVariableRow = {adapter.countReceptacles(idMedicion)+"", cadPolaridad, vfaseneutro+"", vneutrotierra+"", vfasetierra+""};
                            listReceptaclesId.add(subVariableRow);
                            tableAdapterElec.notifyDataSetChanged();
                            dialog.dismiss();
                            showMsg(getActivity(),"¡Nuevo registro de verificación de seguridad eléctrica agregado exitosamente!",1);
                        }
                        else{
                            showMsg(getActivity(),"Ocurrio un error al intentar crear el nuevo registro de subvariable...",2);
                        }
                    }
                    else{
                        showMsg(getActivity(),"Ocurrio un error al intentar crear el nuevo registro de variable...",2);
                    }
                    adapter.close();
                }
            }
        });
        dialog.show();
    }

    //pop up de medidas excepto medidas electricas
    //*************************************************
    //administrar mediciones
    //2-iluminacion ambiental
    //3-iluminacion campo operatorio >>>> ya no!!!!
    //4-ruido ambiental
    //5-temperatura ambiental
    //6-humedad relativa ambietakl
    private void dialogCheckMedL(final int ParamIdMed, final int Paramtipo, String title, final String subtitle){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_chk_med,null);
        builder.setView(dialogView);

        //inicializacion de objetos
        TextView chk_med_title = (TextView) dialogView.findViewById(R.id.chk_med_title);
        TextView chk_med_nec = (TextView) dialogView.findViewById(R.id.chk_med_nec);
        TextView chk_med_strd_value = (TextView) dialogView.findViewById(R.id.chk_med_strd_value);
        TableView<String[]> tblEnviCons = (TableView<String[]>) dialogView.findViewById(R.id.tblEnviCons);
        Button btnCloseChkCon = (Button)dialogView.findViewById(R.id.btnCloseChkCon);
        Button btnNewCon = (Button)dialogView.findViewById(R.id.btnNewCon);

        //Definicione titulo de popup
        chk_med_title.setText(title);

        //definiendo cantintidad de mediciones necesarias y valores estandar
        switch (Paramtipo){
            case 2:     chk_med_nec.setText("Muestras mínimas necesarias: 5");//iluminacion ambiental
                chk_med_strd_value.setText("Valor estándar: 600 Lux Max.");
                break;
            case 4:     chk_med_nec.setText("Muestras mínimas necesarias: 1"); //ruido ambiemtal
                chk_med_strd_value.setText("Valor estándar: 65 dB Max.");
                break;
            case 5:     chk_med_nec.setText("Muestras mínimas necesarias: 2"); //temperatura ambiental
                chk_med_strd_value.setText("Valor estándar: 22°C a 26°C.");
                break;
            case 6:     chk_med_nec.setText("Muestras mínimas necesarias: 2"); //humedad relativa ambiental
                chk_med_strd_value.setText("Valor estándar: 30 % a 60%");
                break;
        }

        //cargar header
        tblEnviCons.setHeaderAdapter(new SimpleTableHeaderAdapter(getActivity(), TABLE_HEADERS_VAR));
        //cargar datos
        adapter.open();
        listConditions = adapter.getVariableList(ParamIdMed,Paramtipo);
        adapter.close();

        //inicializar adaptador
        tblAdapConditions = new SimpleTableDataAdapter(getActivity(), listConditions);
        tblEnviCons.setDataAdapter(tblAdapConditions);
        //cargar funcionalidad de click
        //tableViewElec.addDataClickListener(new AdministrarEquipo.EquipClickListener());
        //personalizacion de la tabla:
        int colorEvenRows = getResources().getColor(R.color.table_even);
        int colorOddRows = getResources().getColor(R.color.table_odd);
        tblEnviCons.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));


        final AlertDialog dialog = builder.create();

        //Listener de los botones guardar y cancelar
        btnCloseChkCon .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnNewCon .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNewMed(ParamIdMed, Paramtipo, subtitle);
                //dialog.dismiss();
            }
        });

        dialog.show();
    }

    //pop up agregar nuevo valor de medicion
    private void dialogNewMed(final int ParamIdMed, final int Paramtipo, String subtitle){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_med,null);
        builder.setView(dialogView);

        //objetos del popup
        final TextView add_med_title = (TextView)dialogView.findViewById(R.id.add_med_title);
        final TextView add_med_subtitle = (TextView)dialogView.findViewById(R.id.add_med_subtitle);
        Button btnCancelMed = (Button)dialogView.findViewById(R.id.btnCancelMed);
        Button btnGetMed = (Button)dialogView.findViewById(R.id.btnGetMed);
        final EditText txt_med_dato = (EditText)dialogView.findViewById(R.id.txt_med_dato);

        //titulo
        add_med_title.setText(subtitle);
        //subtitulo
        //subtitulo
        adapter.open();
        int cantpuntos = adapter.countVariable(idMedicion,Paramtipo)+1;
        add_med_subtitle.setText("Punto de medición P"+cantpuntos);
        adapter.close();

        final AlertDialog dialog = builder.create();
        //Listener de los botones guardar y cancelar
        btnCancelMed .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnGetMed .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                if(checkEditTextIsEmpty(txt_med_dato)){
                    adapter.open();
                    //insertando la variable
                    float EnviCond = Float.parseFloat(txt_med_dato.getText().toString());

                    //verificando standard de cada variable
                    int isok = 0;
                    switch (Paramtipo){
                        case 2: isok = (EnviCond>=0 && EnviCond<=600)? 1 : 0; //iluminacion
                            break;
                        case 4: isok = (EnviCond>=0 && EnviCond<=65)? 1 : 0; //ruido ambiemtal
                            break;
                        case 5: isok = (EnviCond>=22 && EnviCond<=65)? 1 : 0; //temperatura ambiental
                            break;
                        case 6: isok = (EnviCond>=30 && EnviCond<=60)? 1 : 0; //humedad relativa ambiental
                            break;
                    }

                    if(adapter.newVariable(ParamIdMed, EnviCond, Paramtipo,isok)){
                        int lastRegId = 1;
                        if(listConditions.size() > 0){
                            lastRegId = Integer.parseInt(listConditions.get(listConditions.size()-1)[0]) + 1;
                        }
                        //interpretando la bandera de cumplimiento de estandar:
                        String isokString = isok == 1 ? "Si" : "No";
                        //creando nueva linea
                        String[] variableRow = {lastRegId+"", EnviCond+"",isokString};
                        listConditions.add(variableRow);
                        tblAdapConditions.notifyDataSetChanged();
                        dialog.dismiss();
                        //showMsg(getActivity(),"¡Nuevo registro de condición ambiental agregado exitosamente!",1);
                    }
                    else{
                        showMsg(getActivity(),"Ocurrio un error al intentar crear el nuevo registro de variable...",2);
                    }
                    adapter.close();
                }
            }
        });
        dialog.show();
    }


    //pop up de comentarios
    private void dialogGetComment(final int ParamIdMed){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_med_comment,null);
        builder.setView(dialogView);

        //inicializacion de objetos
        final EditText txtComment = (EditText) dialogView.findViewById(R.id.txtComment);
        Button btnCancelComment = (Button)dialogView.findViewById(R.id.btnCancelComment);
        Button btnGetComment = (Button)dialogView.findViewById(R.id.btnGetComment);

        //verificando si ya existe un comentario y mostrarlo
        adapter.open();
        txtComment.setText(adapter.getMeaComment(ParamIdMed));
        adapter.close();

        //inicializando dialog
        final AlertDialog dialog = builder.create();

        //Listener de los botones guardar y cancelar
        btnCancelComment .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnGetComment .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.open();
                if(adapter.updateMeaComment(ParamIdMed, txtComment.getText().toString() )){
                    showMsg(getActivity(),"¡el comentario ha sido registrado exitosamente!",1);
                }
                else {
                    showMsg(getActivity(), "Ocurrio un error al intentar guardar el comentario...", 2);
                }
                adapter.close();
                dialog.dismiss();
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
