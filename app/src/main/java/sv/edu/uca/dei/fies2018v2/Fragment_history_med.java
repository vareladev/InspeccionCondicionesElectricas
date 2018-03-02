package sv.edu.uca.dei.fies2018v2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

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
    private SimpleTableDataAdapter tableAdapter;
    private static ArrayList<String[]> measureList;

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

        //elementos graficos
        Spinner spinSearchBy = (Spinner) view.findViewById(R.id.spinSearchBy);

        //inicializando spinner para criterios de busqueda
        ArrayList<String> criteriosDeBusqueda = new  ArrayList<String>();
        criteriosDeBusqueda.add("Hospital San Rafael");
        criteriosDeBusqueda.add("Hospital de la Mujer");
        criteriosDeBusqueda.add("Hospital Bloom");
        ArrayAdapter<String> SearchByAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, criteriosDeBusqueda);
        spinSearchBy.setAdapter(SearchByAdapter);

        //Inicializacion de la tabla
        tableView = (TableView<String[]>) view.findViewById(R.id.tblHistory);
        //cargar header
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getActivity(), TABLE_HEADERS));
        //cargar datos
        adapter.open();
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
