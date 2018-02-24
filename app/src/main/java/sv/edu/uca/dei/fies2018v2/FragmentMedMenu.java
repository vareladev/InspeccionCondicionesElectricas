package sv.edu.uca.dei.fies2018v2;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import database.Adapter;



public class FragmentMedMenu extends Fragment {
    private Adapter adapter;

    public FragmentMedMenu() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_med_menu, container, false);
        //inicializacion de adaptador de base de datos
        adapter = new Adapter(getActivity());

        LinearLayout MgetElecData = (LinearLayout) view.findViewById(R.id.getElecData);
        MgetElecData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogNewEquipo();
            }
        });
        return view;
    }

    //*************************************************
    //AGREGAR EQUIPO
    private void dialogNewEquipo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_med_electrica,null);
        builder.setView(dialogView);

        //objetos del popup
        Button mBtnCancelSegEle = (Button)dialogView.findViewById(R.id.btnCancelMedElec);
        Button mBtnGetMedElec = (Button)dialogView.findViewById(R.id.btnGetMedElec);

        final AlertDialog dialog = builder.create();
        //Listener de los botones guardar y cancelar

        mBtnCancelSegEle .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mBtnGetMedElec .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
