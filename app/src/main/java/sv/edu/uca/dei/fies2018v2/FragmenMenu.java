package sv.edu.uca.dei.fies2018v2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;

import java.io.File;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class FragmenMenu extends Fragment {



    public FragmenMenu() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragmen_menu, container, false);
        LinearLayout btnEquipo1 = (LinearLayout) view.findViewById(R.id.Menu_boton1); //nuevo registro
        LinearLayout btnEquipo3 = (LinearLayout) view.findViewById(R.id.Menu_boton3); //history
        LinearLayout btnEquipo2 = (LinearLayout) view.findViewById(R.id.Menu_boton2); //equipo
        LinearLayout Menu_boton4 = (LinearLayout) view.findViewById(R.id.Menu_boton4); //sincronizar
        LinearLayout Menu_boton5 = (LinearLayout) view.findViewById(R.id.Menu_boton5); //exportar



        btnEquipo2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((Home)getActivity()).openFragmentEquipo();
            }
        });
        btnEquipo3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((Home)getActivity()).openFragmentHistory();
                //((Home)getActivity()).openVarRegMed();
            }
        });
        btnEquipo1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((Home)getActivity()).openFragNewMed();
                //((Home)getActivity()).openVarRegMed();
            }
        });

        Menu_boton4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
        Menu_boton5.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                File downloadDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                ArrayList<String> tableList = new ArrayList<String>();
                tableList.add("accesorio");
                tableList.add("equipo");
                tableList.add("medicion");
                tableList.add("mxe");
                tableList.add("variable");
                tableList.add("subvariable");

                SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getActivity(), "fies2018.db",downloadDir.toString()+"/");
                sqliteToExcel.exportSpecificTables(tableList, "test2.xls", new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted(String filePath) {
                        Toast.makeText(getActivity(), "Successfully exported database", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getActivity(), "Error in exporting database: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.getStackTrace()[0].toString());
                    }
                });

            }
        });


        return view;

    }




}
