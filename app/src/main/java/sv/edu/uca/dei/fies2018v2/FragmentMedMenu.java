package sv.edu.uca.dei.fies2018v2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;


import database.Adapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import simpletable.SimpleTableHeaderAdapter;


public class FragmentMedMenu extends Fragment {

    //variables auxiliares
    private Adapter adapter;
    private static int idMedicion;

    //para tablas
    private static final String[] TABLE_HEADERS_ELEC = { "R", "Polaridad", "VfaseN", "VNeutroT", "VfaseT" };
    private static ArrayList<String[]> listReceptaclesId;
    private static SimpleTableDataAdapter tableAdapterElec;
    private static final String[] TABLE_HEADERS_VAR = { "Punto", "Valor","Cumple" };
    private static ArrayList<String[]> listConditions;
    private static SimpleTableDataAdapter tblAdapConditions;

    public FragmentMedMenu() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_med_menu, container, false);


        //inicializacion de adaptador de base de datos
        adapter = new Adapter(getActivity());
        //obteniendo ultimo id de la tabla medicion
        adapter.open();
        idMedicion = adapter.getLastId("medicion");
        adapter.close();

        //Botones del menu
        LinearLayout MgetElecData = (LinearLayout) view.findViewById(R.id.getElecData); //verificacion receptaculos
        LinearLayout MgetLuxData = (LinearLayout) view.findViewById(R.id.getLuxData);  //verificacion ilumnacion ambiental
        //LinearLayout MgetLuxRoomData = (LinearLayout) view.findViewById(R.id.getLuxRoomData);
        LinearLayout MgetNoiseData = (LinearLayout) view.findViewById(R.id.getNoiseData); //verificacion ruido ambiental
        LinearLayout MgetTempData = (LinearLayout) view.findViewById(R.id.getTempData); //verificacion temperatura ambiental
        LinearLayout MgetComment = (LinearLayout) view.findViewById(R.id.getComment); //comentario de la medicion
        LinearLayout MgetHumData = (LinearLayout) view.findViewById(R.id.getHumData); //verificacion humedad relativa
        //boton guardar inspeccion
        Button btn_save_insp = (Button) view.findViewById(R.id.btn_save_insp);

        MgetElecData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogChkSegElec();
            }
        });

        MgetLuxData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogCheckMedL(idMedicion, 2, "Condiciones ambientales: Iluminación", "Nuevo Registro: Iluminación");
            }
        });
        /*MgetLuxRoomData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogCheckMedL(idMedicion, 3, "Condiciones ambientales: Iluminación campo operatorio", "Nuevo Registro: Iluminación");
            }
        });*/
        MgetNoiseData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogCheckMedL(idMedicion, 4, "Condiciones ambientales: Ruido", "Nuevo Registro: Ruido");
            }
        });
        MgetTempData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogCheckMedL(idMedicion, 5, "Condiciones ambientales: Temperatura", "Nuevo Registro: Temperatura");
            }
        });
        MgetHumData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogCheckMedL(idMedicion, 6, "Condiciones ambientales: Humedad", "Nuevo Registro: Humedad");
            }
        });
        MgetComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogGetComment(idMedicion);
            }
        });

        btn_save_insp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("¡Ok!");
                builder.setMessage("Los datos han sido guardados exitosamente, ¿Desea regresar a la pantalla principal?");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_warning);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((Home)getActivity()).openMainMenu();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();//showing the dialog
            }
        });

        return view;
    }

    //*************************************************
    //administrar mediciones
    //1-seguridad receptaculos
    //2-iluminacion ambiental
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

    private void dialogNewMed(final int ParamIdMed, final int Paramtipo, String title){
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
        //comentario
        final EditText et_get_comment = (EditText)dialogView.findViewById(R.id.et_get_comment);


        //titulo
        add_med_title.setText(title);
        //subtitulo
        adapter.open();
        add_med_subtitle.setText("Punto de medición P"+adapter.countVariable(idMedicion,Paramtipo)+1);
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
                    //convirtiendo el datoa flotante
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

                    //verificando comentario en variables
                    String comentario = null;
                    //verificando comentario
                    if(checkEditTextIsEmpty(et_get_comment)){
                        comentario = et_get_comment.getText().toString();
                    }

                    adapter.open();
                    //insertando la variable
                    if(adapter.newVariable(ParamIdMed, EnviCond, Paramtipo,isok, comentario)){
                        int lastRegId = 1;
                        if(listConditions.size() > 0){
                            lastRegId = Integer.parseInt(listConditions.get(listConditions.size()-1)[0]) + 1;
                        }

                        //interpretando la bandera de cumplimiento de estandar:
                        String isokString = isok == 1 ? "Si" : "No";
                        //creando nueva linea
                        String[] variableRow = {lastRegId+"", EnviCond+"", isokString};
                        listConditions.add(variableRow);
                        tblAdapConditions.notifyDataSetChanged();
                        dialog.dismiss();
                        //Toast.makeText(getActivity(), "¡Nuevo registro de condición ambiental agregado exitosamente!", Toast.LENGTH_SHORT).show();
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
        //comentario
        final EditText et_get_comment = (EditText)dialogView.findViewById(R.id.et_get_comment);


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
                    if(adapter.newVariable(idMedicion,0, 1,0, null)){
                        //al ser med electrica se llena la tabla subvariable
                        int idVariable = adapter.getLastId("variable");
                        //float polaridad = Float.parseFloat(mTxtPolaridad.getText().toString());
                        int polaridad = 0;
                        if (checkBoxPolaridad.isChecked())
                            polaridad = 1;
                        float vfaseneutro = Float.parseFloat(mTxtVFaseNeutro.getText().toString());
                        float vneutrotierra = Float.parseFloat(mTxtVNeutroTierra.getText().toString());
                        float vfasetierra = Float.parseFloat(mTxtVFaseTierra.getText().toString());

                        String comentario = null;
                        //verificando comentario
                        if(checkEditTextIsEmpty(et_get_comment)){
                            comentario = et_get_comment.getText().toString();
                        }

                        if(adapter.newSubVariable(idVariable,polaridad,vfaseneutro,vneutrotierra,vfasetierra, comentario)){
                            String cadPolaridad = polaridad == 1 ? "Si" : "No";
                            String[] subVariableRow = {adapter.countReceptacles(idMedicion)+"", cadPolaridad, vfaseneutro+"", vneutrotierra+"", vfasetierra+""};
                            listReceptaclesId.add(subVariableRow);
                            tableAdapterElec.notifyDataSetChanged();
                            dialog.dismiss();
                            //showMsg(getActivity(),"¡Nuevo registro de verificación de seguridad eléctrica agregado exitosamente!",1);
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
