package sv.edu.uca.dei.fies2018v2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import Objetos.Area;
import Objetos.Hospital;
import Objetos.Usuario;
import database.Adapter;

import static sv.edu.uca.dei.fies2018v2.MenuMediciones.getSavedObjectFromPreference;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Seg_Elec_1 extends Fragment {
    //Adapter requerido para la base de datos
    private Adapter adapter;

    public Fragment_Seg_Elec_1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__seg__elec_1, container, false);

        //usuario conectado:
        final Usuario currentUser = getSavedObjectFromPreference(getActivity(), "preferences", "currentUser", Usuario.class);

        //Base de datos
        adapter = new Adapter(getActivity());
        adapter.createDatabase();

        //elementos graficos
        Spinner spinnerHospital = (Spinner) view.findViewById(R.id.spinHospitales);
        final EditText mTxtNewMedServicio = (EditText) view.findViewById(R.id.txtNewMedServicio);

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
                mTxtNewMedServicio.setText("UCIN");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        //retornando la vista
        return view;
    }

}
