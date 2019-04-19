package sv.edu.uca.dei.fies2018v2;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import Objetos.SeguridadElectrica;
import Objetos.Usuario;
import database.Adapter;

public class FragmentClase1Tipo1 extends Fragment {
    //adaptador base de datos
    private Adapter adapter;

    //id de la medicion  la que pertenece este cuestionario
    private static int idSegElecMeasure;
    private static int GLobalIdClase;
    private static int GLobalIdTipo;


    public FragmentClase1Tipo1() {
        // Required empty public constructor
    }

    //para recibir parametro
    public static FragmentClase1Tipo1 newInstance(int idSegElec, int clase, int tipo) {
        Bundle bundle = new Bundle();
        bundle.putInt("idSegElec", idSegElec);
        bundle.putInt("clase", clase);
        bundle.putInt("tipo", tipo);
        FragmentClase1Tipo1 fragment = new FragmentClase1Tipo1();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            idSegElecMeasure = bundle.getInt("idSegElec");
            GLobalIdClase = bundle.getInt("clase");
            GLobalIdTipo = bundle.getInt("tipo");
        }
        return;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clase1_tipo1, container, false);

        //usuario conectado:
        final Usuario currentUser = getSavedObjectFromPreference(getActivity(), "preferences", "currentUser", Usuario.class);

        //leyendo parametros de entrada
        readBundle(getArguments());

        //Base de datos
        //adapter = new Adapter(getActivity());
        //adapter.createDatabase();



        //objetos del fragmento
        //subtitulo
        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
        //linear layout
        LinearLayout evaluacion_1 = (LinearLayout) view.findViewById(R.id.evaluacion_1);
        LinearLayout evaluacion_2 = (LinearLayout) view.findViewById(R.id.evaluacion_2);
        LinearLayout evaluacion_3 = (LinearLayout) view.findViewById(R.id.evaluacion_3);
        LinearLayout evaluacion_4 = (LinearLayout) view.findViewById(R.id.evaluacion_4);
        LinearLayout evaluacion_5 = (LinearLayout) view.findViewById(R.id.evaluacion_5);
        LinearLayout evaluacion_6 = (LinearLayout) view.findViewById(R.id.evaluacion_6);
        LinearLayout evaluacion_7 = (LinearLayout) view.findViewById(R.id.evaluacion_7);
        LinearLayout evaluacion_8 = (LinearLayout) view.findViewById(R.id.evaluacion_8);
        LinearLayout evaluacion_9 = (LinearLayout) view.findViewById(R.id.evaluacion_9);

        //imagenes de icono
        //1: inspeccion visual
        final ImageView img_inspeccion_visual = (ImageView) view.findViewById(R.id.img_inspeccion_visual);
        //2: Resistencia de puesta a tierra de protección - Equipos biomédicos Clase I
        final ImageView img_res_tierra = (ImageView) view.findViewById(R.id.img_res_tierra);
        //3: corriente de fuga en equipo
        final ImageView img_corr_fuga = (ImageView) view.findViewById(R.id.img_corr_fuga);
        //4: corriente de fuga paciente
        final ImageView img_corr_fuga_paciente = (ImageView) view.findViewById(R.id.img_corr_fuga_paciente);
        //5: resistencia 1
        final ImageView img_res_1 = (ImageView) view.findViewById(R.id.img_res_1);
        //6: resistencia 2
        final ImageView img_res_2 = (ImageView) view.findViewById(R.id.img_res_2);
        //7: resistencia 3
        final ImageView img_res_3 = (ImageView) view.findViewById(R.id.img_res_3);
        //8: resistencia 4
        final ImageView img_res_4 = (ImageView) view.findViewById(R.id.img_res_4);
        //9: resistencia 5
        final ImageView img_res_5 = (ImageView) view.findViewById(R.id.img_res_5);

        //botones de cada medicion
        //1: inspeccion visual
        Button btn_ivisual = (Button) view.findViewById(R.id.btn_inspeccion_visual);
        //2: Resistencia de puesta a tierra de protección - Equipos biomédicos Clase I
        Button btn_res_tierra = (Button) view.findViewById(R.id.btn_res_tierra);
        //3: corriente de fuga
        Button bnt_corr_fuga = (Button) view.findViewById(R.id.btn_corr_fuga);
        //4: corriente de fuga paciente
        Button btn_corr_fuga_paciente = (Button) view.findViewById(R.id.btn_corr_fuga_paciente);
        //5: resistencia 1
        Button btn_res_1 = (Button) view.findViewById(R.id.btn_res_1);
        //6: resistencia 2
        Button btn_res_2 = (Button) view.findViewById(R.id.btn_res_2);
        //7: resistencia 3
        Button btn_res_3 = (Button) view.findViewById(R.id.btn_res_3);
        //8: resistencia 4
        Button btn_res_4 = (Button) view.findViewById(R.id.btn_res_4);
        //9: resistencia 5
        Button btn_res_5 = (Button) view.findViewById(R.id.btn_res_5);
        //boton para guardar evaluacion
        Button btn_save_eva = (Button) view.findViewById(R.id.btn_save_eva);



        //clase que contiene los popups
        final SeguridadElectrica seguridadElectrica = new SeguridadElectrica();

        //definiendo que evaluaciones se verán
        if(GLobalIdClase==1){ //clase I
            switch (GLobalIdTipo){
                case 1: //tipo B
                    subtitle.setText("Equipo clase I, tipo B");
                    evaluacion_4.setVisibility(LinearLayout.GONE);
                    break;
                case 2: //tipo BF (Se muestran todos!!)
                    subtitle.setText("Equipo clase I, tipo BF");
                    break;
                case 3: //tipo CF (Se muestran todos!!)
                    subtitle.setText("Equipo clase I, tipo CF");
                    break;
            }
        }else if(GLobalIdClase==2){ //clase II
            switch (GLobalIdTipo){
                case 1: //tipo B
                    subtitle.setText("Equipo clase II, tipo B");
                    evaluacion_2.setVisibility(LinearLayout.GONE);
                    evaluacion_4.setVisibility(LinearLayout.GONE);
                    evaluacion_5.setVisibility(LinearLayout.GONE);
                    evaluacion_7.setVisibility(LinearLayout.GONE);
                    break;
                case 2: //tipo BF
                    subtitle.setText("Equipo clase II, tipo BF");
                    evaluacion_2.setVisibility(LinearLayout.GONE);
                    evaluacion_5.setVisibility(LinearLayout.GONE);
                    evaluacion_7.setVisibility(LinearLayout.GONE);
                    break;
                case 3: //tipo CF
                    subtitle.setText("Equipo clase II, tipo CF");
                    evaluacion_2.setVisibility(LinearLayout.GONE);
                    evaluacion_5.setVisibility(LinearLayout.GONE);
                    evaluacion_7.setVisibility(LinearLayout.GONE);
                    break;
            }
        }

        //listeners
        //1: inspeccion visual
        btn_ivisual .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seguridadElectrica.inspeccionVisual(getActivity(),idSegElecMeasure,GLobalIdClase,GLobalIdTipo,img_inspeccion_visual);
            }
        });

        //2: Resistencia de puesta a tierra de protección - Equipos biomédicos Clase I
        btn_res_tierra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seguridadElectrica.resistenciaTierra(getActivity(),idSegElecMeasure,GLobalIdClase,GLobalIdTipo,img_res_tierra);
            }
        });

        //3: fuga de corriente de equipo
        bnt_corr_fuga .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seguridadElectrica.corrienteFugaEquipo(getActivity(),idSegElecMeasure,GLobalIdClase,GLobalIdTipo,img_corr_fuga);
            }
        });

        //4: fuga de corriente paciente
        btn_corr_fuga_paciente .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seguridadElectrica.corrienteFugaPaciente(getActivity(),idSegElecMeasure,GLobalIdClase,GLobalIdTipo,img_corr_fuga_paciente);
            }
        });

        //5: resistencia 1
        btn_res_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seguridadElectrica.resistencia1(getActivity(),idSegElecMeasure,GLobalIdClase,GLobalIdTipo,img_res_1);
            }
        });

        //6: resistencia 2
        btn_res_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seguridadElectrica.resistencia2(getActivity(),idSegElecMeasure,GLobalIdClase,GLobalIdTipo,img_res_2);
            }
        });

        //7: resistencia 3
        btn_res_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seguridadElectrica.resistencia3(getActivity(),idSegElecMeasure,GLobalIdClase,GLobalIdTipo,img_res_3);
            }
        });

        //8: resistencia 4
        btn_res_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seguridadElectrica.resistencia4(getActivity(),idSegElecMeasure,GLobalIdClase,GLobalIdTipo,img_res_4);
            }
        });

        //9: resistencia 5
        btn_res_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seguridadElectrica.resistencia5(getActivity(),idSegElecMeasure,GLobalIdClase,GLobalIdTipo,img_res_5);
            }
        });

        //guardar evaluacion
        btn_save_eva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seguridadElectrica.checkEva(GLobalIdClase,GLobalIdTipo)){
                    showMsg(getActivity(), "ok a guardar", 1);
                }
                else{
                    showMsg(getActivity(), "Para guardar es necesario haber ingresado todas las mediciones requeridas." +
                            "Verifique que cada icono muestre un check verde, esto indica que los datos han sido ingresados" +
                            "correctamente.", 2);
                }
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

}
