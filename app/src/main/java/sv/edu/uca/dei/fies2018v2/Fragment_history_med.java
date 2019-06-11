package sv.edu.uca.dei.fies2018v2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

import Objetos.Area;
import Objetos.Hospital;
import database.Adapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.model.TableColumnWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import simpletable.SimpleTableHeaderAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_history_med extends Fragment {
    //base de datos
    private Adapter adapter;
    //tableview
    private static final String[] TABLE_HEADERS = { "Hospital", "Área", "Fecha", "En linea" };
    private TableView<String[]> tableView;
    private static SimpleTableDataAdapter tableAdapter;
    private static ArrayList<String[]> measureList;
    //para spinner
    private int selectedHospital;


    public Fragment_history_med() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_history_med, container, false);

        //Base de datos
        adapter = new Adapter(getActivity());
        adapter.createDatabase();

        //para spinner hospital
        selectedHospital = 1;

        //elementos graficos
        Spinner spinnerHospital = (Spinner) view.findViewById(R.id.spinSearchBy);
        tableView = (TableView<String[]>) view.findViewById(R.id.tblHistory);
        Button btnSetFilter = (Button) view.findViewById(R.id.btnSetFilter);


        //inicializando spinner para criterios de busqueda
        adapter.open();
        ArrayList<Hospital> hospitalList = adapter.getHospitals();
        adapter.close();
        ArrayAdapter<Hospital> spinHosAdapter = new ArrayAdapter<Hospital>(getActivity(), android.R.layout.simple_spinner_dropdown_item, hospitalList);
        spinnerHospital.setAdapter(spinHosAdapter);


        //Inicializacion de la tabla
        //cargar header
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getActivity(), TABLE_HEADERS));
        //cargar datos
        adapter.open();
        //por el momento todas las mediciones
        measureList  = adapter.getMeasuresList();
        adapter.close();

        tableAdapter = new SimpleTableDataAdapter(getActivity(), measureList);
        tableView.setDataAdapter(tableAdapter);
        //personalizacion de la tabla:
        int colorEvenRows = getResources().getColor(R.color.table_even);
        int colorOddRows = getResources().getColor(R.color.table_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
        //inicializando modelo
        TableColumnWeightModel tableColumnModel = new TableColumnWeightModel( 4 );
        tableColumnModel.setColumnWeight(0, 7);
        tableColumnModel.setColumnWeight(1, 4);
        tableColumnModel.setColumnWeight(2, 4);
        tableColumnModel.setColumnWeight(3, 4);
        tableView.setColumnModel(tableColumnModel);
        //listener del tableview
        //cargar funcionalidad de click
        tableView.addDataClickListener(new tblClickListener());

        //Listener hospital
        spinnerHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Hospital hospital = (Hospital) parent.getSelectedItem();
                selectedHospital = Integer.parseInt(hospital.getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        btnSetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedHospital == 1){
                    adapter.open();
                    ArrayList<String[]> NewMeasureList = adapter.getMeasuresList();
                    adapter.close();
                    measureList.clear();
                    for(String s[]: NewMeasureList){
                        measureList.add(s);
                    }
                    tableAdapter.notifyDataSetChanged();
                }
                else{
                    adapter.open();
                    ArrayList<String[]> NewMeasureList = adapter.getMeasuresListById(selectedHospital);
                    adapter.close();
                    measureList.clear();
                    for(String s[]: NewMeasureList){
                        measureList.add(s);
                    }
                    tableAdapter.notifyDataSetChanged();

                }
            }
        });

        return view;
    }

    private class tblClickListener implements TableDataClickListener<String[]> {
        @Override
        public void onDataClicked(int rowIndex, String[] clickedCar) {
            //Log.v("history", "id de medicion: "+measureList.get(rowIndex)[4]);
            int idMedicion = Integer.parseInt(measureList.get(rowIndex)[4]);
            int idHospital = Integer.parseInt(measureList.get(rowIndex)[5]);
            int idArea = Integer.parseInt(measureList.get(rowIndex)[6]);
            ((Home)getActivity()).openFragmentHistoryDetails(idMedicion, idHospital, idArea);

        }
    }


}
