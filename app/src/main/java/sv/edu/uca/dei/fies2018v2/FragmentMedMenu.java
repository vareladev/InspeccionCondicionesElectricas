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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
        LinearLayout MgetElecData = (LinearLayout) view.findViewById(R.id.getElecData);
        LinearLayout MgetLuxData = (LinearLayout) view.findViewById(R.id.getLuxData);
        LinearLayout MgetLuxRoomData = (LinearLayout) view.findViewById(R.id.getLuxRoomData);
        LinearLayout MgetNoiseData = (LinearLayout) view.findViewById(R.id.getNoiseData);
        LinearLayout MgetTempData = (LinearLayout) view.findViewById(R.id.getTempData);
        LinearLayout MgetHumData = (LinearLayout) view.findViewById(R.id.getHumData);
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
        MgetLuxRoomData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogCheckMedL(idMedicion, 3, "Condiciones ambientales: Iluminación campo operatorio", "Nuevo Registro: Iluminación");
            }
        });
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
        return view;
    }

    //*************************************************
    //administrar mediciones
    //2-iluminacion ambiental}
    //3-iluminacion campo operatorio
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
        TableView<String[]> tblEnviCons = (TableView<String[]>) dialogView.findViewById(R.id.tblEnviCons);
        Button btnCloseChkCon = (Button)dialogView.findViewById(R.id.btnCloseChkCon);
        Button btnNewCon = (Button)dialogView.findViewById(R.id.btnNewCon);

        //Definicione titulo de popup
        chk_med_title.setText(title);

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
        /*adapter.open();
        mTxtvTitleMedElec.setText("Recéptaculo R"+(adapter.countReceptacles(idMedicion)+1));
        adapter.close();*/

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
                    int isok = 1;
                    if(adapter.newVariable(ParamIdMed, EnviCond, Paramtipo,isok)){
                        int lastRegId = 1;
                        if(listConditions.size() > 0){
                            lastRegId = Integer.parseInt(listConditions.get(listConditions.size()-1)[0]) + 1;
                        }
                        String[] variableRow = {lastRegId+"", EnviCond+"", isok+""};
                        listConditions.add(variableRow);
                        tblAdapConditions.notifyDataSetChanged();
                        dialog.dismiss();
                        showMsg(getActivity(),"¡Nuevo registro de condición ambiental agregado exitosamente!",1);
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
        final EditText mTxtPolaridad = (EditText)dialogView.findViewById(R.id.txtPolaridad);
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
                if(checkEditTextIsEmpty(mTxtPolaridad, mTxtVFaseNeutro,mTxtVNeutroTierra,mTxtVFaseTierra )){
                    adapter.open();
                    //insertando la variable
                    if(adapter.newVariable(idMedicion,0, 1,0)){
                        //al ser med electrica se llena la tabla subvariable
                        int idVariable = adapter.getLastId("variable");
                        float polaridad = Float.parseFloat(mTxtPolaridad.getText().toString());
                        float vfaseneutro = Float.parseFloat(mTxtVFaseNeutro.getText().toString());
                        float vneutrotierra = Float.parseFloat(mTxtVNeutroTierra.getText().toString());
                        float vfasetierra = Float.parseFloat(mTxtVFaseTierra.getText().toString());
                        if(adapter.newSubVariable(idVariable,polaridad,vfaseneutro,vneutrotierra,vfasetierra)){
                            String[] subVariableRow = {adapter.countReceptacles(idMedicion)+"", polaridad+"", vfaseneutro+"", vneutrotierra+"", vfasetierra+""};
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
