package sv.edu.uca.dei.fies2018v2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import database.Adapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import simpletable.SimpleTableHeaderAdapter;


public class AdministrarEquipo extends Fragment {

    private Adapter adapter;
    private static final String[] TABLE_HEADERS = { "Nombre", "Marca", "Modelo", "Inventario" };
    private TableView<String[]> tableView;
    private SimpleTableDataAdapter tableAdapter;
    //for datepicker
    private static EditText GlobalTxtFecha;

    //constructor
    public AdministrarEquipo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_administrar_equipo, container, false);

        //inicializacion de adaptador de base de datos
        adapter = new Adapter(getActivity());
        adapter.open();
        String[][] DATA_TO_SHOW  = adapter.getEquipoPreview();
        adapter.close();

        //Inicializacion de la tabla
        tableView = (TableView<String[]>) view.findViewById(R.id.tableView);
        //cargar header
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getActivity(), TABLE_HEADERS));
        //cargar datos
        tableAdapter = new SimpleTableDataAdapter(getActivity(), DATA_TO_SHOW);
        tableView.setDataAdapter(tableAdapter);
        //cargar funcionalidad de click
        tableView.addDataClickListener(new EquipClickListener());
        //personalizacion de la tabla:
        int colorEvenRows = getResources().getColor(R.color.table_even);
        int colorOddRows = getResources().getColor(R.color.table_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
        //modificacion de columnas
        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
        columnModel.setColumnWeight(0,3 );
        columnModel.setColumnWeight(1,1 );
        columnModel.setColumnWeight(2,1 );
        columnModel.setColumnWeight(3,1 );
        tableView.setColumnModel(columnModel);

        //agregar equipo
        Button btnNuevoEquipo = (Button) view.findViewById(R.id.btnNuevoEquipo);
        btnNuevoEquipo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogNewEquipo();
            }
        });

        return view;
    }

    private class EquipClickListener implements TableDataClickListener<String[]> {
        @Override
        public void onDataClicked(int rowIndex, String[] clickedCar) {
            final String[] row = tableAdapter.getItem(rowIndex);
            dialogEditEquipo(Integer.parseInt(row[row.length-1]));
        }
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

    ////*************************************************
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
        else{
            builder.setTitle("???")
                    .setIcon(R.drawable.ic_warning);
        }
        AlertDialog alert = builder.create();
        alert.show();//showing the dialog
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    //*************************************************
    //AGREGAR EQUIPO
    private void dialogNewEquipo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_agregar_equipo,null);
        builder.setView(dialogView);

        //objetos del popup
        Button mBtnEquipCancel = (Button)dialogView.findViewById(R.id.btnEquipCancel);
        Button mBtnEquipSave = (Button)dialogView.findViewById(R.id.btnEquipGuardar);
        Button btnEquipF = (Button)dialogView.findViewById(R.id.btnEquipF);
        final EditText mTxtNombre = (EditText)dialogView.findViewById(R.id.txtNombre_1);
        final EditText mTxtMarca = (EditText)dialogView.findViewById(R.id.txtMarca);
        final EditText mTxtModelo = (EditText)dialogView.findViewById(R.id.txtModelo);
        final EditText mTxtSerie = (EditText)dialogView.findViewById(R.id.txtSerie);
        final EditText mTxtCorrelInv = (EditText)dialogView.findViewById(R.id.txtCorrelInv);
        final EditText mTxtAccesorios = (EditText)dialogView.findViewById(R.id.txtAccesorios);
        GlobalTxtFecha = (EditText)dialogView.findViewById(R.id.txtFecha);
        GlobalTxtFecha.setFocusable(false);
        GlobalTxtFecha.setEnabled(false);

        final AlertDialog dialog = builder.create();
        //Listener de los botones guardar y cancelar
        btnEquipF .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment mDatePicker = new DatePickerFragment();
                mDatePicker.show(getFragmentManager(), "Select date");
            }
        });
        mBtnEquipCancel .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mBtnEquipSave .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Agregar nuevo equipo", Toast.LENGTH_SHORT).show();
                if(checkEditTextIsEmpty(mTxtNombre,mTxtMarca, mTxtModelo)){
                    //Toast.makeText(getActivity(), "Agregar nuevo equipo", Toast.LENGTH_SHORT).show();
                    int correlativo = 0;
                    if(!TextUtils.isEmpty(mTxtCorrelInv.getText()))
                        correlativo = Integer.parseInt(mTxtCorrelInv.getText().toString());

                    adapter.open();
                    if(adapter.newEquip(mTxtNombre.getText().toString(),mTxtMarca.getText().toString(),mTxtModelo.getText().toString(),mTxtSerie.getText().toString(),
                            GlobalTxtFecha.getText().toString(),correlativo)){
                        int idTabla = adapter.getLastId("equipo");

                        //verificando y guardando accesorios
                        if (!isEmpty(mTxtAccesorios)){
                            String[] accesorios = mTxtAccesorios.getText().toString().split(",");
                            adapter.newAccessory(accesorios, idTabla);
                        }
                        //creando fila para actualizar la tabla en la APP
                        final String[] row = {mTxtMarca.getText().toString(),mTxtModelo.getText().toString(),mTxtSerie.getText().toString(),mTxtCorrelInv.getText().toString(),null,Integer.toString(idTabla)};
                        tableAdapter.add(row);
                        tableAdapter.notifyDataSetChanged();
                        adapter.close();
                        dialog.dismiss();
                        showMsg(getActivity(), "¡Nuevo equipo guardado con Éxito!", 1);
                    }
                    else{
                        adapter.close();
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "ERROR Agregar nuevo equipo", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.show();
    }

    //*************************************************
    //EDITAR EQUIPO
    private void dialogEditEquipo(final int idEquip){
        final int id_equipo = idEquip;

        //popup para fecha
        AlertDialog.Builder alertDialogBuilderSuccess = new AlertDialog.Builder( getActivity());
        alertDialogBuilderSuccess.setTitle("TopUp Success");
        // set dialog message
        final AlertDialog alertDialogSuccess = alertDialogBuilderSuccess.create();
        alertDialogBuilderSuccess
                .setMessage(
                        "Message...")
                .setCancelable(false)
                .setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialogSuccess.cancel();
                            }
                        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_editar_equipo,null);
        builder.setView(dialogView);

        //objetos del popup
        Button mBtnEquipCancel = (Button)dialogView.findViewById(R.id.btnEditEquipC);
        Button mBtnEquipDel = (Button)dialogView.findViewById(R.id.btnEditEquipG);
        final Button mBtnEquipEdit = (Button)dialogView.findViewById(R.id.btnEditEquipE);
        final Button mBtnEquipDate = (Button)dialogView.findViewById(R.id.btnEditEquipF);
        final EditText txtNombre_2 = (EditText)dialogView.findViewById(R.id.txtNombre_2);
        final EditText mTxtMarca = (EditText)dialogView.findViewById(R.id.txtEditMarca);
        final EditText mTxtModelo = (EditText)dialogView.findViewById(R.id.txtEditModel);
        final EditText mTxtSerie = (EditText)dialogView.findViewById(R.id.txtEditSerie);
        final EditText mTxtCorrelInv = (EditText)dialogView.findViewById(R.id.txtEditCorrel);
        final EditText mTxtAccesorios = (EditText)dialogView.findViewById(R.id.txtEditTools);
        GlobalTxtFecha = (EditText)dialogView.findViewById(R.id.txtEditFecha);
        final ListView mListEditEquip = (ListView)dialogView.findViewById(R.id.ListEditEquip);

        //para lista
        adapter.open();
        final ArrayAdapter<String> ListAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, adapter.getTools(id_equipo));
        adapter.close();
        mListEditEquip.setAdapter(ListAdapter);
        mListEditEquip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popUpDelTool(position, ListAdapter);
            }
        });

        //obteniendo informacion desde base de datos del equipo
        adapter.open();
        ArrayList<String> mArrayList = adapter.getEquipoDetail(id_equipo);
        adapter.close();
        //set information:
        //Nombre
        txtNombre_2.setText(mArrayList.get(5));
        txtNombre_2.setFocusable(false);
        txtNombre_2.setEnabled(false);
        //Marca
        mTxtMarca.setText(mArrayList.get(0));
        mTxtMarca.setFocusable(false);
        mTxtMarca.setEnabled(false);
        //modelo
        mTxtModelo.setText(mArrayList.get(1));
        mTxtModelo.setFocusable(false);
        mTxtModelo.setEnabled(false);
        //serie
        mTxtSerie.setText(mArrayList.get(2));
        mTxtSerie.setFocusable(false);
        mTxtSerie.setEnabled(false);
        //correlativo
        mTxtCorrelInv.setText(mArrayList.get(3));
        mTxtCorrelInv.setFocusable(false);
        mTxtCorrelInv.setEnabled(false);
        //accesorios
        mTxtAccesorios.setFocusable(false);
        mTxtAccesorios.setEnabled(false);
        //fecha
        GlobalTxtFecha.setText(mArrayList.get(4));
        GlobalTxtFecha.setFocusable(false);
        GlobalTxtFecha.setEnabled(false);
        mBtnEquipDate.setEnabled(false);


        final AlertDialog dialog = builder.create();
        //Listener de los botones guardar y cancelar
        mBtnEquipDate .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment mDatePicker = new DatePickerFragment();
                mDatePicker.show(getFragmentManager(), "Select date");
            }
        });
        mBtnEquipCancel .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mBtnEquipEdit .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mTxtMarca.isEnabled()){
                    //Nombre
                    txtNombre_2.setEnabled(true);
                    txtNombre_2.setClickable(true);
                    txtNombre_2.setFocusableInTouchMode(true);
                    txtNombre_2.setFocusable(true);
                    //Marca
                    mTxtMarca.setEnabled(true);
                    mTxtMarca.setClickable(true);
                    mTxtMarca.setFocusableInTouchMode(true);
                    mTxtMarca.setFocusable(true);
                    //modelo
                    mTxtModelo.setEnabled(true);
                    mTxtModelo.setClickable(true);
                    mTxtModelo.setFocusableInTouchMode(true);
                    mTxtModelo.setFocusable(true);
                    //serie
                    mTxtSerie.setEnabled(true);
                    mTxtSerie.setClickable(true);
                    mTxtSerie.setFocusableInTouchMode(true);
                    mTxtSerie.setFocusable(true);
                    //correlativo
                    mTxtCorrelInv.setEnabled(true);
                    mTxtCorrelInv.setClickable(true);
                    mTxtCorrelInv.setFocusableInTouchMode(true);
                    mTxtCorrelInv.setFocusable(true);
                    //accesorios
                    mTxtAccesorios.setEnabled(true);
                    mTxtAccesorios.setClickable(true);
                    mTxtAccesorios.setFocusableInTouchMode(true);
                    mTxtAccesorios.setFocusable(true);
                    //fecha
                    mBtnEquipDate.setEnabled(true);
                    mBtnEquipEdit.setText("Guardar equipo");
                }
                else{
                    //String[] updateRow = {id, marca, modelo, serie, correlativo, fechadecalibracion, nombre};/
                    if(checkEditTextIsEmpty(txtNombre_2,mTxtMarca, mTxtModelo)){
                        String[] updateRow = {id_equipo+"", mTxtMarca.getText().toString(),
                                mTxtModelo.getText().toString(),null,null, null, txtNombre_2.getText().toString()};
                        if(TextUtils.isEmpty(mTxtSerie.getText()))
                            updateRow[3] = null;
                        else
                            updateRow[3] = mTxtSerie.getText().toString();
                        if(TextUtils.isEmpty(mTxtCorrelInv.getText()))
                            updateRow[4] = null;
                        else
                            updateRow[4] = mTxtCorrelInv.getText().toString();
                        if(TextUtils.isEmpty(GlobalTxtFecha.getText()))
                            updateRow[5] = null;
                        else
                            updateRow[5] = GlobalTxtFecha.getText().toString();

                        Log.e("FRAGMEN", "Guardar >>"+ Arrays.toString(updateRow));
                        //dialog.dismiss();
                        adapter.open();
                        try{
                            //eliminar todos los accesorios
                            adapter.delAllTools(id_equipo);
                            //actualizar los accesorios de la listview
                            String ListToolls[] = new String[mListEditEquip.getCount()];
                            for(int i=0; i < mListEditEquip.getCount(); i++) {
                                ListToolls[i] = ListAdapter.getItem(i);
                            }
                            //guardar lista
                            adapter.newAccessory(ListToolls,id_equipo);
                            //verificando y guardando accesorios
                            if (!isEmpty(mTxtAccesorios)){
                                String[] accesorios = mTxtAccesorios.getText().toString().split(",");
                                adapter.newAccessory(accesorios, id_equipo);
                            }
                            //actualizar el equipo
                            adapter.updateEquipo(updateRow);
                            showMsg(getActivity(), "¡Los datos del equipo han sido actualizados con Éxito!", 1);
                        }
                        catch (Exception e){
                            showMsg(getActivity(), "¡Ocurrió un error al intentar actualizar la base de datos!", 2);
                        }
                        finally {
                            adapter.close();
                            dialog.dismiss();
                            //actualizando tabla
                            adapter.open();
                            String[][] updatedData  = adapter.getEquipoPreview();
                            adapter.close();
                            tableAdapter.clear();
                            tableAdapter.addAll(updatedData);
                            tableAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        mBtnEquipDel .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                AlertDialog PopUpDelAlert = popUpDelEquip(id_equipo);
                PopUpDelAlert.show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //*************************************************
    //SELECCIONAR FECHA
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(getActivity(),this, year, month, day);
            dpd.getDatePicker().setCalendarViewShown(false);
            return dpd;
        }
        public void onDateSet(DatePicker view, int year, int month, int day) {
            GlobalTxtFecha.setText(String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day));
        }
    }

    //*************************************************
    //ELIMINAR REGISTRO
    public AlertDialog popUpDelEquip(final int idEquipo){
        //popup delete
        AlertDialog.Builder popUpDelBuilder = new AlertDialog.Builder(getActivity());
        popUpDelBuilder.setTitle("Eliminar Registro")
                .setMessage("¿Esta seguro que desea eliminar este registro?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        adapter.open();
                        try{
                            adapter.deteleteEquip(idEquipo);
                            showMsg(getActivity(), "¡El registro con id de equipo: "+idEquipo+", ha sido eliminado con éxito!", 1);
                        }
                        catch (Exception e){
                            showMsg(getActivity(), "¡Ocurrió un error al intentar eliminar el registro en la base de datos!", 2);
                        }
                        finally {
                            adapter.close();
                            dialog.dismiss();
                            //actualizando tabla
                            adapter.open();
                            String[][] updatedData  = adapter.getEquipoPreview();
                            adapter.close();
                            tableAdapter.clear();
                            tableAdapter.addAll(updatedData);
                            tableAdapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
        AlertDialog PopUpDelAlert = popUpDelBuilder.create();
        return PopUpDelAlert;
    }

    //*************************************************
    //ELIMINAR REGISTRO
    public void popUpDelTool(final int position, final ArrayAdapter<String> ListAdapter){
        //popup delete
        AlertDialog.Builder popUpDelBuilder = new AlertDialog.Builder(getActivity());
        popUpDelBuilder.setTitle("Eliminar Registro")
                .setMessage("¿Esta seguro que desea eliminar el accesorio: "+ListAdapter.getItem(position)+"?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListAdapter.remove(ListAdapter.getItem(position));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
        AlertDialog PopUpDelAlert = popUpDelBuilder.create();
        PopUpDelAlert.show();
    }
}
