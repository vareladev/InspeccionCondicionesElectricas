package sv.edu.uca.dei.fies2018v2;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import database.Adapter;
import Objetos.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuMediciones extends Fragment {
    //Database adapter
    private Adapter adapter;

    public static Equipo ListItemFromLeft;
    public static Equipo ListItemFromRight;


    public MenuMediciones() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_mediciones, container, false);

        //usuario conectado:
        final Usuario currentUser = getSavedObjectFromPreference(getActivity(), "preferences", "currentUser", Usuario.class);

        //Base de datos
        adapter = new Adapter(getActivity());
        adapter.createDatabase();

        //elementos graficos
        Spinner spinnerHospital = (Spinner) view.findViewById(R.id.spinHospitales);
        final Spinner mSpinAreas = (Spinner) view.findViewById(R.id.spinAreas);
        final ListView mlistEquipFromDB = (ListView) view.findViewById(R.id.listEquipFromDB);
        final ListView mlistEquipFromSelected = (ListView) view.findViewById(R.id.listEquipFromSelected);
        final ImageView mImgNuevaMedArea = (ImageView) view.findViewById(R.id.imgNuevaMedArea);
        Button mBtnPlusEquip = (Button) view.findViewById(R.id.btnPlusEquip);
        Button mBtnMinusEquip = (Button) view.findViewById(R.id.btnMinusEquip);
        final Button mBtnNuevaMedicion = (Button) view.findViewById(R.id.btnNuevaMedicion);
        final EditText mTxtNewMedServicio = (EditText) view.findViewById(R.id.txtNewMedServicio);
        final EditText mTxtNewMedResp = (EditText) view.findViewById(R.id.txtNewMedResp);

        //lista de areas
        //creando arreglo (vacio por el momento, esto depende del hospital seleccionado)
        final ArrayList<Area> AreasList = new ArrayList<>();
        final ArrayAdapter<Area> spinAreaAdapter = new ArrayAdapter<Area>(getActivity(), android.R.layout.simple_spinner_dropdown_item, AreasList);
        mSpinAreas.setAdapter(spinAreaAdapter);
        mSpinAreas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), "area seleccionada", Toast.LENGTH_SHORT).show();
                mImgNuevaMedArea.setImageBitmap(AreasList.get(position).getPlano());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Listas y tablas
        //lista de hospitales
        adapter.open();
        ArrayList<Hospital> hospitalList = adapter.getHospitals();
        adapter.close();
        ArrayAdapter<Hospital> spinHosAdapter = new ArrayAdapter<Hospital>(getActivity(), android.R.layout.simple_spinner_dropdown_item, hospitalList);
        spinnerHospital.setAdapter(spinHosAdapter);
        spinnerHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Hospital hospital = (Hospital) parent.getSelectedItem();
                adapter.open();
                ArrayList<Area> AreasListAux = adapter.getAreas(hospital.getId());
                adapter.close();
                //pasando los datos a la lista de area
                AreasList.clear();
                for (Area a : AreasListAux) {
                    AreasList.add(a);
                }
                //notificando cambios
                spinAreaAdapter.notifyDataSetChanged();
                //cambiando imagen
                mImgNuevaMedArea.setImageBitmap(AreasList.get(0).getPlano());
                if(position == 0)
                    mBtnNuevaMedicion.setEnabled(false);
                else
                    mBtnNuevaMedicion.setEnabled(true);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //lista equipos guardados en base de datos LEFT
        adapter.open();
        final ArrayList<Equipo> equipList = adapter.getEquipsForList();
        adapter.close();
        final ArrayAdapter<Equipo> listEquipAdapter = new ArrayAdapter<Equipo>(getActivity(), android.R.layout.simple_spinner_dropdown_item, equipList);
        mlistEquipFromDB.setAdapter(listEquipAdapter);
        mlistEquipFromDB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < mlistEquipFromDB.getChildCount(); i++) {
                    if(position == i ){
                        ListItemFromLeft = (Equipo) equipList.get(position);
                        //Toast.makeText(getContext(), "equipo ID: "+ultimoEquipSeleccionado.getId()+",  equipo Name : "+ultimoEquipSeleccionado.getEquipo(), Toast.LENGTH_SHORT).show();
                        mlistEquipFromDB.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.primary_light));
                    }else{
                        mlistEquipFromDB.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });

        //lista equipos a usar en la medicion RIGHT
        final ArrayList<Equipo> equipList2 = new ArrayList<>();
        final ArrayAdapter<Equipo> listEquipAdapter2 = new ArrayAdapter<Equipo>(getActivity(), android.R.layout.simple_spinner_dropdown_item, equipList2);
        mlistEquipFromSelected.setAdapter(listEquipAdapter2);
        mlistEquipFromSelected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < mlistEquipFromSelected.getChildCount(); i++) {
                    if(position == i ){
                        ListItemFromRight = (Equipo) equipList2.get(position);
                        mlistEquipFromSelected.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.primary_light));
                    }else{
                        mlistEquipFromSelected.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });

        //Listener boton de añadir equipo a la medicion
        mBtnPlusEquip .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(ListItemFromLeft == null)){
                    //Toast.makeText(getContext(), "equipo ID: "+ListItemFromLeft.getId()+",  equipo Name : "+ListItemFromLeft.getEquipo(), Toast.LENGTH_SHORT).show();
                    //coloreando todas las posiciones del listview
                    for (int i = 0; i < mlistEquipFromDB.getChildCount(); i++)
                        mlistEquipFromDB.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    //removiendo de lista LEFT el elemento selccionado
                    listEquipAdapter.remove(ListItemFromLeft);
                    listEquipAdapter.notifyDataSetChanged();
                    //agregando a la lista RIGHT el elemento seleccionado
                    equipList2.add(ListItemFromLeft);
                    listEquipAdapter2.notifyDataSetChanged();
                    //volviendo a variable auxiliar equipo nulo
                    ListItemFromLeft = null;
                }
            }
        });

        //listener boton de eliminar equipo de la medicion
        mBtnMinusEquip .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(ListItemFromRight == null)){
                    //Toast.makeText(getContext(), "equipo ID: "+ListItemFromRight.getId()+",  equipo Name : "+ListItemFromRight.getEquipo(), Toast.LENGTH_SHORT).show();
                    //coloreando todas las posiciones del listview
                    for (int i = 0; i < mlistEquipFromSelected.getChildCount(); i++)
                        mlistEquipFromSelected.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    //removiendo de lista RIGHT el elemento selccionado
                    listEquipAdapter2.remove(ListItemFromRight);
                    listEquipAdapter2.notifyDataSetChanged();
                    //agregando a la lista LEFT el elemento seleccionado
                    equipList.add(ListItemFromRight);
                    listEquipAdapter.notifyDataSetChanged();
                    //volviendo a variable auxiliar equipo nulo
                    ListItemFromRight = null;
                }
            }
        });


        //listener boton de eliminar equipo de la medicion
        mBtnNuevaMedicion .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean res = true;
                String idArea = ((Area) mSpinAreas.getSelectedItem()).getId();
                NuevaMedicion nuevaMedicion = new NuevaMedicion(currentUser.getId(), idArea, mTxtNewMedServicio.getText().toString(), mTxtNewMedResp.getText().toString(), "0000-0000");
                //equipList2
                //haciendo insert de nueva medicion
                adapter.open();
                if(adapter.mewMeasurement(nuevaMedicion)){ //insert exitoso!!
                    int idMedicion = adapter.getLastId("medicion");
                    for (Equipo e : equipList2) {
                        if (!adapter.newMxeReg(idMedicion, e)){
                            showMsg(getActivity(),"¡Error! El registro de nueva medición no se esta guardando correctamente, existe un problema con la asignación de equipo a la medición.",2);
                            res = false;
                            break;
                        }
                    }
                    if(res){
                        //Log.v("new","ultimo id de medicion: "+adapter.getLastId("medicion")+", ultimo id de mxe: "+adapter.getLastId("mxe"));
                        showMsg(getActivity(),"¡El nuevo registro ha sido creado correctamente, ahora puede realizar la inspeción de instalaciones electricas y condiciones ambientales",1);
                        ((Home)getActivity()).openVarRegMed();
                    }

                }
                else{ //algo falló
                    showMsg(getActivity(),"¡Error! No se pudo crear el registro de nueva medición",2);
                }
                adapter.close();
            }
        });

        return view;
    }

    public static <GenericClass> GenericClass getSavedObjectFromPreference(Context context, String preferenceFileName, String preferenceKey, Class<GenericClass> classType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        if (sharedPreferences.contains(preferenceKey)) {
            final Gson gson = new Gson();
            return gson.fromJson(sharedPreferences.getString(preferenceKey, ""), classType);
        }
        return null;
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
}
