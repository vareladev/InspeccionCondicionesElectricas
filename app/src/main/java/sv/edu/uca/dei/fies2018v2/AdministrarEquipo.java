package sv.edu.uca.dei.fies2018v2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import database.Adapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import simpletable.SimpleTableHeaderAdapter;


public class AdministrarEquipo extends Fragment {

    private Adapter adapter;
    private static final String[] TABLE_HEADERS = { "Marca", "Modelo", "Serie", "N° Inventario" };
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

    private void dialogNewEquipo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_agregar_equipo,null);
        builder.setView(dialogView);

        //objetos del popup
        Button mBtnEquipCancel = (Button)dialogView.findViewById(R.id.btnEquipCancel);
        Button mBtnEquipSave = (Button)dialogView.findViewById(R.id.btnEquipGuardar);
        final EditText mTxtMarca = (EditText)dialogView.findViewById(R.id.txtMarca);
        final EditText mTxtModelo = (EditText)dialogView.findViewById(R.id.txtModelo);
        final EditText mTxtSerie = (EditText)dialogView.findViewById(R.id.txtSerie);
        final EditText mTxtCorrelInv = (EditText)dialogView.findViewById(R.id.txtCorrelInv);
        final EditText mTxtAccesorios = (EditText)dialogView.findViewById(R.id.txtAccesorios);

        final AlertDialog dialog = builder.create();
        //Listener de los botones guardar y cancelar
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
                if(checkEditTextIsEmpty(mTxtMarca, mTxtModelo)){
                    //Toast.makeText(getActivity(), "Agregar nuevo equipo", Toast.LENGTH_SHORT).show();
                    adapter.open();
                    if(adapter.newEquip(mTxtMarca.getText().toString(),mTxtModelo.getText().toString(),mTxtSerie.getText().toString(),Integer.parseInt(mTxtCorrelInv.getText().toString()))){
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
                    }
                    else{
                        Toast.makeText(getActivity(), "ERROR Agregar nuevo equipo", Toast.LENGTH_SHORT).show();
                    }
                    adapter.close();
                    dialog.dismiss();
                    showMsg(getActivity(), "¡Nuevo equipo guardado con Éxito!", 1);
                }
            }
        });
        dialog.show();
    }

    private void dialogEditEquipo(int idEquip){
        final int id_equipo = idEquip;


        //popup para fecha
        AlertDialog.Builder alertDialogBuilderSuccess = new AlertDialog.Builder( getActivity());
        alertDialogBuilderSuccess.setTitle("TopUp Success");
        // set dialog message
        final AlertDialog alertDialogSuccess = alertDialogBuilderSuccess.create();
        alertDialogBuilderSuccess
                .setMessage(
                        "You voucher is printed, please go to the cashier.")
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
        Button mBtnEquipSave = (Button)dialogView.findViewById(R.id.btnEditEquipG);
        Button mBtnEquipEdit = (Button)dialogView.findViewById(R.id.btnEditEquipE);
        final Button mBtnEquipDate = (Button)dialogView.findViewById(R.id.btnEditEquipF);
        final EditText mTxtMarca = (EditText)dialogView.findViewById(R.id.txtEditMarca);
        final EditText mTxtModelo = (EditText)dialogView.findViewById(R.id.txtEditModel);
        final EditText mTxtSerie = (EditText)dialogView.findViewById(R.id.txtEditSerie);
        final EditText mTxtCorrelInv = (EditText)dialogView.findViewById(R.id.txtEditCorrel);
        final EditText mTxtAccesorios = (EditText)dialogView.findViewById(R.id.txtEditTools);
        GlobalTxtFecha = (EditText)dialogView.findViewById(R.id.txtEditFecha);
        final ListView mListEditEquip = (ListView)dialogView.findViewById(R.id.ListEditEquip);

        //para lista
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };
        ArrayAdapter<String> ListAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mListEditEquip.setAdapter(ListAdapter);

        //obteniendo informacion desde base de datos del equipo
        adapter.open();
        ArrayList<String> mArrayList = adapter.getEquipoDetail(id_equipo);
        adapter.close();
        //set information:
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
                //alertDialogSuccess.show();
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
            }
        });
        mBtnEquipSave .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

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
            //Toast.makeText(getActivity(), "Selected date: " + String.valueOf(year) + " - " + String.valueOf(month) + " - " + String.valueOf(day), Toast.LENGTH_SHORT).show();
            GlobalTxtFecha.setText(String.valueOf(year) + " - " + String.valueOf(month) + " - " + String.valueOf(day));
        }
    }
}
