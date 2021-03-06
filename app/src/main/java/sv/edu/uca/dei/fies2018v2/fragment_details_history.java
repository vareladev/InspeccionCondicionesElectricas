package sv.edu.uca.dei.fies2018v2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import Objetos.Area;
import Objetos.Equipo;
import Objetos.NuevaMedicion;
import database.Adapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_details_history extends Fragment {
    private int idMedicion;
    private int idHospital;
    private int idArea;
    private static String fechaMedicion;

    //Database adapter
    private Adapter adapter;

    public fragment_details_history() {
        // Required empty public constructor
    }

    //para recibir parametro
    public static fragment_details_history newInstance(int idMedicion, int idHospital, int idArea) {
        Bundle bundle = new Bundle();
        bundle.putInt("idMedicion", idMedicion);
        bundle.putInt("idHospital", idHospital);
        bundle.putInt("idArea", idArea);
        fragment_details_history fragment = new fragment_details_history();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            idMedicion = bundle.getInt("idMedicion");
            idHospital = bundle.getInt("idHospital");
            idArea = bundle.getInt("idArea");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_details_history, container, false);

        //leyendo parametros de entrada
        readBundle(getArguments());
        //Log.v("Details History", "idRecibido: " +idMedicion+", idhospita: "+idHospital+", idArea"+idArea);

        //Base de datos
        adapter = new Adapter(getActivity());
        adapter.createDatabase();

        //elementos graficos
        final EditText txt_h_hospital = (EditText) view.findViewById(R.id.txt_h_hospital);
        final EditText txt_h_area = (EditText) view.findViewById(R.id.txt_h_area);
        final EditText txt_h_servicio = (EditText) view.findViewById(R.id.txt_h_servicio);
        final EditText txt_h_responsable = (EditText) view.findViewById(R.id.txt_h_responsable);
        final ImageView img_h_plano = (ImageView) view.findViewById(R.id.img_h_plano);
        final ListView listEquipSelected = (ListView) view.findViewById(R.id.listEquipSelected);
        final Button btnMedDetalles = (Button) view.findViewById(R.id.btnMedDetalles);


        //llenando datos
        adapter.open();
        txt_h_hospital.setText(adapter.getHospitalName(idHospital));
        final Area area = adapter.getArea(idArea);
        txt_h_area.setText(area.getArea());
        img_h_plano.setImageBitmap(area.getPlano());
        final NuevaMedicion nuevaMedicion = adapter.getMeasure(idMedicion);
        fechaMedicion = nuevaMedicion.getFecha();
        txt_h_servicio.setText(nuevaMedicion.getServicioAnalizado());
        txt_h_responsable.setText(nuevaMedicion.getResponsable());
        disableEditText(txt_h_hospital, txt_h_area, txt_h_responsable, txt_h_servicio);
        final ArrayList<Equipo> measureEquip = adapter.getEquipForEdit(idMedicion, true );
        final ArrayList<Equipo> EquipList = adapter.getEquipForEdit(idMedicion, false);
        adapter.close();


        //llenando listas
        final ArrayAdapter<Equipo> equipListAdapter = new ArrayAdapter<Equipo>(getActivity(), android.R.layout.simple_spinner_dropdown_item, measureEquip);
        listEquipSelected.setAdapter(equipListAdapter);

        //funcionalidad de boton
        btnMedDetalles .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Home)getActivity()).openFragmentMeasureDetails(idMedicion, fechaMedicion);
            }
        });

        return view;
    }

    //DEFINIR CAMPOS OBLIGATORIOS
    public void disableEditText(EditText... editTexts){
        for (EditText editText : editTexts){
            editText.setFocusable(false);
            editText.setEnabled(false);
        }
    }

}
