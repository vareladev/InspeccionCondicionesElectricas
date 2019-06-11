package sv.edu.uca.dei.fies2018v2;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import Objetos.Area;
import Objetos.Clase;
import Objetos.Equipo;
import Objetos.Hospital;
import Objetos.Tipo;
import Objetos.Usuario;
import database.Adapter;

import static sv.edu.uca.dei.fies2018v2.MenuMediciones.getSavedObjectFromPreference;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Seg_Elec_1 extends Fragment {
    //Adapter requerido para la base de datos
    private Adapter adapter;

    //++++++++++++++++++++++++++++++++++
    static ArrayList<String> idEquipList = new ArrayList<>();
    //++++++++++++++++++++++++++++++++++


    public Fragment_Seg_Elec_1() {
        // Required empty public constructor
    }

    //para recibir parametro
    public static Fragment_Seg_Elec_1 newInstance(ArrayList<String> idEquipList) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("lista", idEquipList);
        Fragment_Seg_Elec_1 fragment = new Fragment_Seg_Elec_1();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            idEquipList = bundle.getStringArrayList("lista");
        }
        return;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__seg__elec_1, container, false);

        //usuario conectado:
        final Usuario currentUser = getSavedObjectFromPreference(getActivity(), "preferences", "currentUser", Usuario.class);

        //leyendo parametros de entrada
        readBundle(getArguments());

        //Base de datos
        adapter = new Adapter(getActivity());
        adapter.createDatabase();

        //elementos graficos
        //informacion general
        final Spinner spinnerHospital = (Spinner) view.findViewById(R.id.spinHospitales);
        final EditText txt_servicio = (EditText) view.findViewById(R.id.txt_servicio);
        final EditText txt_responsable = (EditText) view.findViewById(R.id.txt_responsable);
        //dispositivo medico analizado
        final EditText dev_name = (EditText) view.findViewById(R.id.txt_med_device_name);
        final EditText dev_brand = (EditText) view.findViewById(R.id.txt_med_device_brand);
        final EditText dev_model = (EditText) view.findViewById(R.id.txt_med_device_model);
        final EditText dev_ns = (EditText) view.findViewById(R.id.txt_med_device_ns);
        final EditText dev_inv = (EditText) view.findViewById(R.id.txt_med_device_inventario);
        final Spinner spin_clase = (Spinner) view.findViewById(R.id.spin_clase);
        final Spinner spin_tipo = (Spinner) view.findViewById(R.id.spin_tipo);
        //equipo de medicion a utilizar
        final ListView lista_equipo_seg_elec = (ListView) view.findViewById(R.id.lista_equipo_seg_elec);
        //boton crear nuevo registro
        final Button btn_nuevo_registro = (Button) view.findViewById(R.id.btn_nuevo_registro);

        //cargando datos predefinidos
        //cargando Sping de clase de equipo
        adapter.open();
        ArrayList<Clase> listaClase = adapter.getClass4Equip();
        adapter.close();
        ArrayAdapter<Clase> spin_clase_adapter = new ArrayAdapter<Clase>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listaClase);
        spin_clase.setAdapter(spin_clase_adapter);
        //cargando Sping de clase de tipo
        adapter.open();
        ArrayList<Tipo> listaTipo = adapter.getType4Equip();
        adapter.close();
        ArrayAdapter<Tipo> spin_tipo_adapter = new ArrayAdapter<Tipo>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listaTipo);
        spin_tipo.setAdapter(spin_tipo_adapter);

        //definiendo servicio analizado.
        txt_servicio.setText("UCIN");

        //Cargando hospitales
        //obteniendo lista de hospitales desde base de datos
        adapter.open();
        ArrayList<Hospital> hospitalList = adapter.getHospitals();
        adapter.close();
        //creando adaptador para el spin de hospitales
        ArrayAdapter<Hospital> spinHosAdapter = new ArrayAdapter<Hospital>(getActivity(), android.R.layout.simple_spinner_dropdown_item, hospitalList);
        spinnerHospital.setAdapter(spinHosAdapter);
        //Listener del spinner de hospitales
        spinnerHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    btn_nuevo_registro.setEnabled(false);
                    btn_nuevo_registro.setBackground(getResources().getDrawable(R.drawable.shape_disabled_button));
                }
                else {
                    btn_nuevo_registro.setEnabled(true);
                    btn_nuevo_registro.setBackground(getResources().getDrawable(R.drawable.shape_round_corners));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //lista equipos a usar en la medicion
        final ArrayList<Equipo> EquipList = new ArrayList<>();
        //recorriendo lista de ids
        adapter.open();
        for (String idEquipo : idEquipList){
            EquipList.add(adapter.getEquipForNewMea(idEquipo));
        }
        adapter.close();
        final ArrayAdapter<Equipo> listEquipAdapter2 = new ArrayAdapter<Equipo>(getActivity(), android.R.layout.simple_spinner_dropdown_item, EquipList);
        lista_equipo_seg_elec.setAdapter(listEquipAdapter2);

        //listeners
        btn_nuevo_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //guardando la info de equipo analizado
                int idHospital =Integer.parseInt(((Hospital) spinnerHospital.getSelectedItem()).getId());
                int idTipo =  ((Tipo) spin_tipo.getSelectedItem()).getId();
                int idClase =  ((Clase) spin_clase.getSelectedItem()).getId();
                if(checkEditTextIsEmpty(dev_name, dev_brand, dev_model,dev_ns,dev_inv)){
                    adapter.open();
                    boolean result1 = adapter.insertNewEquiAna(dev_name.getText().toString(),
                                            dev_brand.getText().toString(),
                                            dev_model.getText().toString(),
                                            dev_ns.getText().toString(),
                                            dev_inv.getText().toString(),
                                            idClase,
                                            idTipo);
                    if(result1 && checkEditTextIsEmpty(txt_responsable,txt_servicio)){
                        //si se pudo insertar el registro de nueva medicion
                        int lastIdEquip = adapter.getLastId("equipoAnalizado");
                        //Log.e("agregando","ultimo ID de equipo:"+lastIdEquip);
                        //obteniendo fecha de creacion
                        Calendar rightNow = Calendar.getInstance();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String date = df.format(rightNow.getTime());
                        //obteniedo hora
                        int hour24format = rightNow.get(Calendar.HOUR_OF_DAY);
                        int minutes = rightNow.get(Calendar.MINUTE);
                        int segundos = rightNow.get(Calendar.SECOND);
                        String hora = hour24format+":"+minutes+":"+segundos;

                        boolean result2 = adapter.insertNewElecSec(idHospital,
                                            txt_servicio.getText().toString(),
                                            txt_responsable.getText().toString(),
                                            lastIdEquip,
                                            currentUser.getId(),
                                            date,
                                            hora);
                        if(result2){
                            boolean result3=false;
                            //ultimo paso registrar el equipo utilizado por la medicion
                            int lastIdMeasure = adapter.getLastId("segelectrica");
                            //Log.e("agregando","ultimo ID de medicion:"+lastIdMeasure);
                            int lastIdsxe;
                            for (Equipo e : EquipList){
                                if (!adapter.InsertIntosxe(lastIdMeasure, Integer.parseInt(e.getId()))){
                                    showMsg(getActivity(),"¡Error! El registro de nueva medición no se esta guardando correctamente, existe un problema con la asignación de equipo a la medición.",2);
                                    result3=false;
                                    break;
                                }
                                else{
                                    lastIdsxe = adapter.getLastId("sxe");
                                    result3 = true;
                                    //Log.e("agregando cruz sxe","ultimo ID de medicion:"+lastIdsxe);
                                }
                            }
                            if(result3){
                                //evaluando el tipo de medicion
                                ((Home)getActivity()).openFragc1tB(lastIdMeasure, idClase, idTipo);
                                /*if(idClase ==1 && idTipo==1){ //Clase I(1), tipo B(1)
                                    ((Home)getActivity()).openFragc1tB(lastIdMeasure, 1, 1);
                                }*/
                            }
                        }
                    }
                    adapter.close();
                }

                //haciendo insert de nueva medicion
                /*adapter.open();
                if(adapter.mewMeasurement(nuevaMedicion)){ //insert exitoso!!
                    int idMedicion = adapter.getLastId("medicion");
                    for (Equipo e : EquipList) {
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
                adapter.close();*/
            }
        });

        //retornando la vista
        return view;
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
