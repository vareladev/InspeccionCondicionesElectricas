package sv.edu.uca.dei.fies2018v2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


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


        return view;

    }




}
