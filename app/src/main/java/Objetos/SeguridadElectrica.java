package Objetos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import sv.edu.uca.dei.fies2018v2.R;

/**
 * Created by DEI on 17/04/2019.
 */

public class SeguridadElectrica {
    //variable globales  para 1: inspeccion visual.
    private int p1,p2,p3,p4,p5,p6,p7,p8;
    private boolean eva1finished;
    //variables globales para 2: Resistencia de puesta a tierra de protección - Equipos biomédicos Clase I
    private boolean evaResTierraFinished;
    private int comprobacionConector;
    private int IntegridadConector;
    private int opcionMarcada; //posibles valores: 1,2,3,4
    private float evaResTierra;
    //variables globales para 3: Corriente de fuga del equipo
    private boolean evaFugaEquipoFinished;
    private int tipo_fuga_equipo;
    private float eva_fuga_equipo;
    //variables globales para 4: Corriente de fuga del equipo
    private boolean evaFugaPacienteFinished;
    private int tipo_fuga_paciente;
    private float eva_fuga_paciente;
    //variables globales para 5: resistencia tensión de alimentación y protección de puesta a tierra
    private boolean evaRes1Finished;
    private float eva_res_1;
    //variables globales para 6: resistencia del aislamiento entre tensión de alimentación y partes conductivas sin puesta a tierra
    private boolean evaRes2Finished;
    private float eva_res_2;
    //variables globales para 7: resistencia del aislamiento entre partes aplicadas y protección de puesta a tierra
    private boolean evaRes3Finished;
    private float eva_res_3;
    //variables globales para 8: resistencia del aislamiento entre partes aplicadas y partes conductivas sin puesta a tierra
    private boolean evaRes4Finished;
    private float eva_res_4;
    //variables globales para 9: resistencia del aislamiento entre tensión de alimentación y partes aplicadas
    private boolean evaRes5Finished;
    private float eva_res_5;

    /*
    * 1- eva1finished
    * 2- evaResTierraFinished
    * 3-evaFugaEquipoFinished
    * 4-evaFugaPacienteFinished
    * 5-evaRes1Finished
    * 6-evaRes2Finished
    * 7-evaRes3Finished
    * 8-evaRes4Finished
    * 9-evaRes5Finished
    * */
    //evaluadores para saber si la verificacion de seguridad electrica ha sido terminada
    public boolean checkEva(int idClase, int idTipo){
        if(idClase==1){ //clase I
            switch (idTipo){
                case 1: //tipo B
                    if(eva1finished && evaResTierraFinished && evaFugaEquipoFinished
                            && evaRes1Finished && evaRes2Finished && evaRes3Finished && evaRes4Finished && evaRes5Finished){
                        return true;
                    }
                    else{
                        return false;
                    }
                case 2: //tipo BF (Se muestran todos!!)
                    if(eva1finished && evaResTierraFinished && evaFugaEquipoFinished && evaFugaPacienteFinished
                            && evaRes1Finished && evaRes2Finished && evaRes3Finished && evaRes4Finished && evaRes5Finished){
                        return true;
                    }
                    else{
                        return false;
                    }
                case 3: //tipo CF (Se muestran todos!!)
                    if(eva1finished && evaResTierraFinished && evaFugaEquipoFinished && evaFugaPacienteFinished
                            && evaRes1Finished && evaRes2Finished && evaRes3Finished && evaRes4Finished && evaRes5Finished){
                        return true;
                    }
                    else{
                        return false;
                    }
            }
        }else if(idClase==2){ //clase II
            switch (idTipo){
                case 1: //tipo B
                    if(eva1finished && evaFugaEquipoFinished && evaRes2Finished && evaRes4Finished && evaRes5Finished){
                        return true;
                    }
                    else{
                        return false;
                    }
                case 2: //tipo BF
                    if(eva1finished && evaFugaEquipoFinished && evaFugaPacienteFinished && evaRes2Finished
                            && evaRes4Finished && evaRes5Finished){
                        return true;
                    }
                    else{
                        return false;
                    }
                case 3: //tipo CF
                    if(eva1finished && evaFugaEquipoFinished && evaFugaPacienteFinished && evaRes2Finished
                            && evaRes4Finished && evaRes5Finished){
                        return true;
                    }
                    else{
                        return false;
                    }
            }
        }
        return false;
    }


    //constructor
    public SeguridadElectrica (){
        this.eva1finished = false;
        this.evaFugaEquipoFinished = false;
        this.evaRes1Finished = false;
        this.evaRes2Finished = false;
        this.evaRes3Finished = false;
        this.evaRes4Finished = false;
        this.evaRes5Finished = false;

    }

    //1: Pop-up: inspeccion visual
    public void inspeccionVisual(final Context context, int idMedicion, int idClase, int idTipo, final ImageView img){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_inspeccion_visual,null);
        builder.setView(dialogView);
        //objetos de popup
        final Button btn_new_reg = (Button) dialogView.findViewById(R.id.btn_new_reg);
        final RadioGroup rg1 = (RadioGroup) dialogView.findViewById(R.id.rg_iv_1);
        final RadioGroup rg2 = (RadioGroup) dialogView.findViewById(R.id.rg_iv_2);
        final RadioGroup rg3 = (RadioGroup) dialogView.findViewById(R.id.rg_iv_3);
        final RadioGroup rg4 = (RadioGroup) dialogView.findViewById(R.id.rg_iv_4);
        final RadioGroup rg5 = (RadioGroup) dialogView.findViewById(R.id.rg_iv_5);
        final RadioGroup rg6 = (RadioGroup) dialogView.findViewById(R.id.rg_iv_6);
        final RadioGroup rg7 = (RadioGroup) dialogView.findViewById(R.id.rg_iv_7);
        final RadioGroup rg8 = (RadioGroup) dialogView.findViewById(R.id.rg_iv_8);
        //veficando si ya se ha realizado la evaluacion
        if(eva1finished){
            if(p1 == 1){ ((RadioButton)rg1.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg1.getChildAt(1)).setChecked(true); }
            if(p2 == 1){ ((RadioButton)rg2.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg2.getChildAt(1)).setChecked(true); }
            if(p3 == 1){ ((RadioButton)rg3.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg3.getChildAt(1)).setChecked(true); }
            if(p4 == 1){ ((RadioButton)rg4.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg4.getChildAt(1)).setChecked(true); }
            if(p5 == 1){ ((RadioButton)rg5.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg5.getChildAt(1)).setChecked(true); }
            if(p6 == 1){ ((RadioButton)rg6.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg6.getChildAt(1)).setChecked(true); }
            if(p7 == 1){ ((RadioButton)rg7.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg7.getChildAt(1)).setChecked(true); }
            if(p8 == 1){ ((RadioButton)rg8.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg8.getChildAt(1)).setChecked(true); }
        }
        //creando popup
        final AlertDialog dialog = builder.create();
        //listeners
        btn_new_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //evaluando si todos los puntos se respondieron
                if (rg1.getCheckedRadioButtonId() == -1
                        || rg2.getCheckedRadioButtonId() == -1
                        || rg3.getCheckedRadioButtonId() == -1
                        || rg4.getCheckedRadioButtonId() == -1
                        || rg5.getCheckedRadioButtonId() == -1
                        || rg6.getCheckedRadioButtonId() == -1
                        || rg7.getCheckedRadioButtonId() == -1
                        || rg8.getCheckedRadioButtonId() == -1) {
                    showMsg(context, "Debes evaluar y responder cada elemento de cuestionario", 0);
                }
                else {
                    //registrando respuestas
                    p1 = rg1.getCheckedRadioButtonId() == R.id.rg_iv_1_si ? 1 : 0;
                    p2 = rg2.getCheckedRadioButtonId() == R.id.rg_iv_2_si ? 1 : 0;
                    p3 = rg3.getCheckedRadioButtonId() == R.id.rg_iv_3_si ? 1 : 0;
                    p4 = rg4.getCheckedRadioButtonId() == R.id.rg_iv_4_si ? 1 : 0;
                    p5 = rg5.getCheckedRadioButtonId() == R.id.rg_iv_5_si ? 1 : 0;
                    p6 = rg6.getCheckedRadioButtonId() == R.id.rg_iv_6_si ? 1 : 0;
                    p7 = rg7.getCheckedRadioButtonId() == R.id.rg_iv_7_si ? 1 : 0;
                    p8 = rg8.getCheckedRadioButtonId() == R.id.rg_iv_8_si ? 1 : 0;
                    //Log.e("inspeccion visual","resultado del check:"+p1+p2+p3+p4+p5+p6+p7+p8);
                    img.setImageResource(R.drawable.segelec_1_ok);
                    eva1finished = true;
                    dialog.dismiss();
                    showMsg(context, "¡Inspección visual realizada correctamente!", 1);
                }
            }
        });
        //mostrando popup
        dialog.show();
    }

    //2: Resistencia de puesta a tierra de protección - Equipos biomédicos Clase I
    public void resistenciaTierra(final Context context, final int idMedicion, final int idClase, final int idTipo, final ImageView img){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_res_puesta_tierra,null);
        builder.setView(dialogView);
        //objetos de popup
        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
        final RadioGroup rg1 = (RadioGroup) dialogView.findViewById(R.id.rg_iv_1);
        final RadioGroup rg2 = (RadioGroup) dialogView.findViewById(R.id.rg_iv_2);
        final Button btn_sig = (Button) dialogView.findViewById(R.id.btn_sig);

        //definiendo titulo:
        txt_title.setText("Resistencia de puesta a tierra de protección - inspección");
        //veficando si ya se ha realizado la evaluacion
        if(evaResTierraFinished){
            if(comprobacionConector == 1){ ((RadioButton)rg1.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg1.getChildAt(1)).setChecked(true); }
            if(IntegridadConector == 1){ ((RadioButton)rg2.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg2.getChildAt(1)).setChecked(true); }
        }
        //creando popup
        final AlertDialog dialog = builder.create();
        //listeners
        btn_sig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //evaluando si todos los puntos se respondieron
                if (rg1.getCheckedRadioButtonId() == -1  || rg2.getCheckedRadioButtonId() == -1) {
                    showMsg(context, "Debes evaluar y responder cada punto antes de continuar.", 0);
                }
                else{
                    comprobacionConector = rg1.getCheckedRadioButtonId() == R.id.rg_iv_1_si ? 1 : 0;
                    IntegridadConector = rg2.getCheckedRadioButtonId() == R.id.rg_iv_2_si ? 1 : 0;
                    dialog.dismiss();
                    resistenciaTierraSig(context,idMedicion,idClase,idTipo,img);
                }
            }
        });
        //mostrando popup
        dialog.show();
    }

    //2B: Resistencia de puesta a tierra de protección - Equipos biomédicos Clase I
    public void resistenciaTierraSig(final Context context, int idMedicion, int idClase, int idTipo, final ImageView img){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_res_puesta_tierra_2,null);
        builder.setView(dialogView);
        //objetos de popup
        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
        final RadioButton radio_opc_1 = (RadioButton)dialogView.findViewById(R.id.radio_opc_1);
        final RadioButton radio_opc_2 = (RadioButton)dialogView.findViewById(R.id.radio_opc_2);
        final RadioButton radio_opc_3 = (RadioButton)dialogView.findViewById(R.id.radio_opc_3);
        final RadioButton radio_opc_4 = (RadioButton)dialogView.findViewById(R.id.radio_opc_4);
        final EditText txt_opc_1 = (EditText)dialogView.findViewById(R.id.txt_res_1);
        final EditText txt_opc_2 = (EditText)dialogView.findViewById(R.id.txt_res_2);
        final EditText txt_opc_3 = (EditText)dialogView.findViewById(R.id.txt_res_3);
        final EditText txt_opc_4 = (EditText)dialogView.findViewById(R.id.txt_res_4);
        final Button btn_new_reg = (Button)dialogView.findViewById(R.id.btn_new_reg);

        //definiendo titulo:
        txt_title.setText("Resistencia de puesta a tierra de protección - Registro");
        //deshabilitando todas las respuestas:
        txt_opc_1.setEnabled(false);
        txt_opc_2.setEnabled(false);
        txt_opc_3.setEnabled(false);
        txt_opc_4.setEnabled(false);
        //veficando si ya se ha realizado la evaluacion
        if(evaResTierraFinished){
            switch (opcionMarcada){
                case 1: radio_opc_1.setChecked(true);
                        txt_opc_1.setEnabled(true);
                        txt_opc_1.setText(evaResTierra+"");
                        break;
                case 2: radio_opc_2.setChecked(true);
                        txt_opc_2.setEnabled(true);
                        txt_opc_2.setText(evaResTierra+"");
                        break;
                case 3: radio_opc_3.setChecked(true);
                        txt_opc_3.setEnabled(true);
                        txt_opc_3.setText(evaResTierra+"");
                        break;
                case 4: radio_opc_4.setChecked(true);
                        txt_opc_4.setEnabled(true);
                        txt_opc_4.setText(evaResTierra+"");
                        break;
            }
        }
        //creando popup
        final AlertDialog dialog = builder.create();
        //listeners
        radio_opc_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_opc_2.setChecked(false);
                radio_opc_3.setChecked(false);
                radio_opc_4.setChecked(false);
                txt_opc_1.setEnabled(true);
                txt_opc_2.setEnabled(false);
                txt_opc_3.setEnabled(false);
                txt_opc_4.setEnabled(false);
            }
        });
        radio_opc_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_opc_1.setChecked(false);
                radio_opc_3.setChecked(false);
                radio_opc_4.setChecked(false);
                txt_opc_1.setEnabled(false);
                txt_opc_2.setEnabled(true);
                txt_opc_3.setEnabled(false);
                txt_opc_4.setEnabled(false);
            }
        });
        radio_opc_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_opc_1.setChecked(false);
                radio_opc_2.setChecked(false);
                radio_opc_4.setChecked(false);
                txt_opc_1.setEnabled(false);
                txt_opc_2.setEnabled(false);
                txt_opc_3.setEnabled(true);
                txt_opc_4.setEnabled(false);
            }
        });
        radio_opc_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_opc_1.setChecked(false);
                radio_opc_2.setChecked(false);
                radio_opc_3.setChecked(false);
                txt_opc_1.setEnabled(false);
                txt_opc_2.setEnabled(false);
                txt_opc_3.setEnabled(false);
                txt_opc_4.setEnabled(true);
            }
        });

        btn_new_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcionMarcada = -1;
                if(radio_opc_1.isChecked() && checkEditTextIsEmpty(txt_opc_1)){
                    try{
                        //dato ingresado
                        evaResTierra = Float.parseFloat(txt_opc_1.getText().toString());
                        //definiendo cual opcion marco
                        opcionMarcada=1;
                    }
                    catch(NumberFormatException e) {
                        showMsg(context, "El formato del dato ingresado es incorrecto. Verificar dato ingresado.", 0);
                    }
                }
                else if(radio_opc_2.isChecked() && checkEditTextIsEmpty(txt_opc_2)){
                    try{
                        //dato ingresado
                        evaResTierra = Float.parseFloat(txt_opc_2.getText().toString());
                        //definiendo cual opcion marco
                        opcionMarcada=2;
                    }
                    catch(NumberFormatException e) {
                        showMsg(context, "El formato del dato ingresado es incorrecto. Verificar dato ingresado.", 0);
                    }
                }
                else if(radio_opc_3.isChecked() && checkEditTextIsEmpty(txt_opc_3)){
                    try{
                        //dato ingresado
                        evaResTierra = Float.parseFloat(txt_opc_3.getText().toString());
                        //definiendo cual opcion marco
                        opcionMarcada=3;
                    }
                    catch(NumberFormatException e) {
                        showMsg(context, "El formato del dato ingresado es incorrecto. Verificar dato ingresado.", 0);
                    }
                }
                else if(radio_opc_4.isChecked() && checkEditTextIsEmpty(txt_opc_4)){
                    try{
                        //dato ingresado
                        evaResTierra = Float.parseFloat(txt_opc_4.getText().toString());
                        //definiendo cual opcion marco
                        opcionMarcada=4;
                    }
                    catch(NumberFormatException e) {
                        showMsg(context, "El formato del dato ingresado es incorrecto. Verificar dato ingresado.", 0);
                    }
                }else{ //ningun elemento marcado
                    showMsg(context, "Debes seleccionar y responder una opción.", 0);
                }

                if(opcionMarcada > 0){
                    //variable bandera para precargar datos si ya han sido ingresados
                    evaResTierraFinished = true;
                    //cambia el icono del menu
                    img.setImageResource(R.drawable.seg_elec_2_ok);
                    //cierra ventana
                    dialog.dismiss();
                    //notifica la accion
                    showMsg(context, "¡Inspección de resistencia de puesta a tierra de protección registrada correctamente!", 1);
                }
            }
        });
        //mostrando popup
        dialog.show();
    }

    //3: Pop-up: Corriente de fuga del equipo
    public void corrienteFugaEquipo(final Context context, int idMedicion, int idClase, int idTipo, final ImageView img){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_corriente_fuga_equipo,null);
        builder.setView(dialogView);
        //elementos del popup
        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
        TextView txt_elemento_eva = (TextView)dialogView.findViewById(R.id.txt_elemento_eva);
        final RadioGroup rg_1 = (RadioGroup) dialogView.findViewById(R.id.rg_met_corr_fuga);
        final RadioButton radio_alter = (RadioButton) dialogView.findViewById(R.id.radio_met_alternativo);
        final RadioButton radio_direc = (RadioButton) dialogView.findViewById(R.id.radio_met_directo);
        final EditText txt_corr_fuga_equi = (EditText) dialogView.findViewById(R.id.txt_corr_fuga_equi);
        final Button btn_new_reg = (Button) dialogView.findViewById(R.id.btn_new_reg);
        //definiendo dato a solicitar a partir de la clase
        if(idClase==1){ //Clase I
            txt_title.setText("Corriente de fuga del equipo (Clase I)");
            txt_elemento_eva.setText("Corriente de fuga del equipo para partes conductivas " +
                    "accesibles de equipos biomédicos clase I conectados o no " +
                    "conectados al conductor de tierra de protección:");
            radio_alter.setText("Método Alternativo (Estándar: 1000 uA max.)");
            radio_direc.setText("Método directo o diferencial (Estándar: 500 uA max.)");
        }
        else if(idClase==2) { //Clase II
            txt_title.setText("Corriente de fuga del equipo (Clase II)");
            txt_elemento_eva.setText("Corriente de fuga del equipo para equipos biomédicos clase II:");
            radio_alter.setText("Método Alternativo (Estándar: 500 uA max.)");
            radio_direc.setText("Método directo o diferencial (Estándar: 100 uA max.)");
        }
        //llenando datos si es que la evaluacion ya se realizo:
        if(evaFugaEquipoFinished){
            if(tipo_fuga_equipo == 0){ ((RadioButton)rg_1.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg_1.getChildAt(1)).setChecked(true); }
            txt_corr_fuga_equi.setText(eva_fuga_equipo+"");
        }
        //creando popup
        final AlertDialog dialog = builder.create();
        //Listener
        btn_new_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rg_1.getCheckedRadioButtonId() != -1 && checkEditTextIsEmpty(txt_corr_fuga_equi)){
                    //verificando el dato ingresado:
                    try{
                        //dato ingresado
                        eva_fuga_equipo = Float.parseFloat(txt_corr_fuga_equi.getText().toString());
                        //tipo de medicion (0 - alternativo, 1-directo o diferencial)
                        tipo_fuga_equipo = rg_1.getCheckedRadioButtonId() == R.id.radio_met_alternativo ? 0 : 1; //0 - alternativo, 1-directo o diferencial
                        //variable bandera para precargar datos si ya han sido ingresados
                        evaFugaEquipoFinished = true;
                        //cambia el icono del menu
                        img.setImageResource(R.drawable.segelec_3_ok);
                        //cierra ventana
                        dialog.dismiss();
                        //notifica la accion
                        showMsg(context, "¡Inspección de corriente de fuga del equipo registrada correctamente!", 1);
                    }
                    catch(NumberFormatException e) {
                        showMsg(context, "El formato del dato ingresado es incorrecto. Verificar dato ingresado.", 0);
                    }
                }
                else{
                    showMsg(context, "Debes evaluar y responder cada elemento de cuestionario", 0);
                }
            }
        });
        //mostrando popup
        dialog.show();
    }


    //4: Pop-up: Corriente de fuga del paciente
    public void corrienteFugaPaciente(final Context context, int idMedicion, int idClase, int idTipo, final ImageView img){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_corriente_fuga_paciente,null);
        builder.setView(dialogView);
        //elementos del popup
        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
        TextView txt_elemento_eva = (TextView)dialogView.findViewById(R.id.txt_elemento_eva);
        final RadioGroup rg_1 = (RadioGroup) dialogView.findViewById(R.id.rg_met_corr_fuga);
        final RadioButton radio_alter = (RadioButton) dialogView.findViewById(R.id.radio_met_alternativo);
        final RadioButton radio_direc = (RadioButton) dialogView.findViewById(R.id.radio_met_directo);
        final EditText txt_corr_paciente_equi = (EditText) dialogView.findViewById(R.id.txt_corr_fuga_paciente);
        final Button btn_new_reg = (Button) dialogView.findViewById(R.id.btn_new_reg);
        //definiendo dato a solicitar a partir de la clase y tipo
        if(idClase==1){ //Clase I
            txt_title.setText("Corriente de fuga de la parte aplicada en contacto con el paciente (Clase I)");
        }
        else
        if(idClase==2) { //Clase II
            txt_title.setText("Corriente de fuga de la parte aplicada en contacto con el paciente (Clase II)");
        }

        switch (idTipo){
            case 1: //tipo B
                radio_alter.setText("No aplica");
                radio_direc.setText("No aplica");
                break;
            case 2: //tipo BF
                radio_alter.setText("Método Alternativo (Estándar tipo BF: 5000 uA)");
                radio_direc.setText("Método directo (Estándar tipo BF: 5000 uA)");
                break;
            case 3: //tipo CF
                radio_alter.setText("Método Alternativo (Estándar tipo CF: 50 uA)");
                radio_direc.setText("Método directo (Estándar tipo CF: 50 uA)");
                break;
        }
        //llenando datos si es que la evaluacion ya se realizo:
        if(evaFugaPacienteFinished){
            if(tipo_fuga_paciente == 0){ ((RadioButton)rg_1.getChildAt(0)).setChecked(true); }
            else { ((RadioButton)rg_1.getChildAt(1)).setChecked(true); }
            txt_corr_paciente_equi.setText(eva_fuga_paciente+"");
        }
        //creando popup
        final AlertDialog dialog = builder.create();
        //Listener
        btn_new_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rg_1.getCheckedRadioButtonId() != -1 && checkEditTextIsEmpty(txt_corr_paciente_equi)){
                    //verificando el dato ingresado:
                    try{
                        //dato ingresado
                        eva_fuga_paciente = Float.parseFloat(txt_corr_paciente_equi.getText().toString());
                        //tipo de medicion (0 - alternativo, 1-directo o diferencial)
                        tipo_fuga_paciente = rg_1.getCheckedRadioButtonId() == R.id.radio_met_alternativo ? 0 : 1; //0 - alternativo, 1-directo o diferencial
                        //variable bandera para precargar datos si ya han sido ingresados
                        evaFugaPacienteFinished = true;
                        //cambia el icono del menu
                        img.setImageResource(R.drawable.segelec_3_ok);
                        //cierra ventana
                        dialog.dismiss();
                        //notifica la accion
                        showMsg(context, "Inspección de corriente de fuga de la parte aplicada en contacto con el paciente realizada correctamente.", 1);
                    }
                    catch(NumberFormatException e) {
                        showMsg(context, "El formato del dato ingresado es incorrecto. Verificar dato ingresado.", 0);
                    }
                }
                else{
                    showMsg(context, "Debes evaluar y responder cada elemento de cuestionario", 0);
                }
            }
        });
        //mostrando popup
        dialog.show();
    }

    //5: resistencia tensión de alimentación y protección de puesta a tierra
    public void resistencia1(final Context context, int idMedicion, int idClase, int idTipo, final ImageView img){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_resistencia_1,null);
        builder.setView(dialogView);
        //elementos del popup
        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
        TextView desc_estandar = (TextView)dialogView.findViewById(R.id.desc_estandar);
        TextView txt_elemento_eva = (TextView)dialogView.findViewById(R.id.txt_elemento_eva);
        final EditText txt_dato = (EditText) dialogView.findViewById(R.id.txt_dato);
        final Button btn_new_reg = (Button) dialogView.findViewById(R.id.btn_new_reg);
        //definiendo tituto:
        String clase=null;
        switch (idClase){
            case 1: clase = "I";
                    break;
            case 2: clase = "II";
                    break;
        }
        String tipo = null;
        switch (idTipo){
            case 1: tipo = "B";
                break;
            case 2: tipo = "BF";
                break;
            case 3: tipo = "CF";
                break;
        }
        txt_title.setText("Evaluación de resistencias. Equipo clase: "+clase+", tipo: "+tipo+".");
        //definiendo estandar:
        String stndr = null;
        if(idClase ==1){ //Clase I
            switch (idTipo){
                case 1: stndr = "Clase I, Tipo B: ≥2 MΩ"; //tipo B
                    break;
                case 2: stndr = "Clase I, Tipo BF: ≥2 MΩ"; //tipo BF
                    break;
                case 3: stndr = "Clase I, Tipo CF: ≥2 MΩ"; //tipo CF
                    break;
            }
        }
        desc_estandar.setText(stndr);
        txt_elemento_eva.setText("Medición de la resistencia del aislamiento entre tensión de alimentación y protección de puesta a tierra.");
        //llenando datos si es que la evaluacion ya se realizo:
        if(evaRes1Finished){
            txt_dato.setText(eva_res_1+"");
        }
        //creando popup
        final AlertDialog dialog = builder.create();
        btn_new_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEditTextIsEmpty(txt_dato)){
                    //verificando el dato ingresado:
                    try{
                        //dato ingresado
                        eva_res_1 = Float.parseFloat(txt_dato.getText().toString());
                        //variable bandera para precargar datos si ya han sido ingresados
                        evaRes1Finished = true;
                        //cambia el icono del menu
                        img.setImageResource(R.drawable.seg_elec_2_ok);
                        //cierra ventana
                        dialog.dismiss();
                        //notifica la accion
                        showMsg(context, "¡Inspección de resistencia registrada correctamente!", 1);
                    }
                    catch(NumberFormatException e) {
                        showMsg(context, "El formato del dato ingresado es incorrecto. Verificar dato ingresado.", 0);
                    }
                }
                else{
                    showMsg(context, "Debes evaluar y responder cada elemento de cuestionario", 0);
                }
            }
        });
        //mostrando popup
        dialog.show();
    }


    //6: resistencia del aislamiento entre tensión de alimentación y partes conductivas sin puesta a tierra
    public void resistencia2(final Context context, int idMedicion, int idClase, int idTipo, final ImageView img){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_resistencia_1,null);
        builder.setView(dialogView);
        //elementos del popup
        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
        TextView desc_estandar = (TextView)dialogView.findViewById(R.id.desc_estandar);
        TextView txt_elemento_eva = (TextView)dialogView.findViewById(R.id.txt_elemento_eva);
        final EditText txt_dato = (EditText) dialogView.findViewById(R.id.txt_dato);
        final Button btn_new_reg = (Button) dialogView.findViewById(R.id.btn_new_reg);
        //definiendo tituto:
        String clase = getClase(idClase);
        String tipo = getTipo(idTipo);

        txt_title.setText("Evaluación de resistencias. Equipo clase: "+clase+", tipo: "+tipo+".");
        //definiendo estandar:
        String stndr = null;
        if(idClase ==1){ //Clase I
            switch (idTipo){
                case 1: stndr = "Clase I, Tipo B: ≥7 MΩ"; //tipo B
                    break;
                case 2: stndr = "Clase I, Tipo BF: ≥7 MΩ"; //tipo BF
                    break;
                case 3: stndr = "Clase I, Tipo CF: ≥7 MΩ"; //tipo CF
                    break;
            }
        }else if(idClase ==2){  //Clase II
            switch (idTipo){
                case 1: stndr = "Clase II, Tipo B: ≥7 MΩ"; //tipo B
                    break;
                case 2: stndr = "Clase II, Tipo BF: ≥7 MΩ"; //tipo BF
                    break;
                case 3: stndr = "Clase II, Tipo CF: ≥7 MΩ"; //tipo CF
                    break;
            }
        }
        desc_estandar.setText(stndr);
        txt_elemento_eva.setText("Medición de la resistencia del aislamiento entre tensión de alimentación y partes conductivas sin puesta a tierra.");
        //llenando datos si es que la evaluacion ya se realizo:
        if(evaRes2Finished){
            txt_dato.setText(eva_res_2+"");
        }
        //creando popup
        final AlertDialog dialog = builder.create();
        btn_new_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEditTextIsEmpty(txt_dato)){
                    //verificando el dato ingresado:
                    try{
                        //dato ingresado
                        eva_res_2 = Float.parseFloat(txt_dato.getText().toString());
                        //variable bandera para precargar datos si ya han sido ingresados
                        evaRes2Finished = true;
                        //cambia el icono del menu
                        img.setImageResource(R.drawable.seg_elec_2_ok);
                        //cierra ventana
                        dialog.dismiss();
                        //notifica la accion
                        showMsg(context, "¡Inspección de resistencia registrada correctamente!", 1);
                    }
                    catch(NumberFormatException e) {
                        showMsg(context, "El formato del dato ingresado es incorrecto. Verificar dato ingresado.", 0);
                    }
                }
                else{
                    showMsg(context, "Debes evaluar y responder cada elemento de cuestionario", 0);
                }
            }
        });
        //mostrando popup
        dialog.show();
    }


    //7: Medición de la resistencia del aislamiento entre partes aplicadas y protección de puesta a tierra
    public void resistencia3(final Context context, int idMedicion, int idClase, int idTipo, final ImageView img){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_resistencia_1,null);
        builder.setView(dialogView);
        //elementos del popup
        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
        TextView desc_estandar = (TextView)dialogView.findViewById(R.id.desc_estandar);
        TextView txt_elemento_eva = (TextView)dialogView.findViewById(R.id.txt_elemento_eva);
        final EditText txt_dato = (EditText) dialogView.findViewById(R.id.txt_dato);
        final Button btn_new_reg = (Button) dialogView.findViewById(R.id.btn_new_reg);
        //definiendo tituto:
        String clase = getClase(idClase);
        String tipo = getTipo(idTipo);

        txt_title.setText("Evaluación de resistencias. Equipo clase: "+clase+", tipo: "+tipo+".");
        //definiendo estandar:
        String stndr = null;
        if(idClase ==1){ //Clase I
            switch (idTipo){
                case 1: stndr = "Clase I, Tipo B: ≥70 MΩ"; //tipo B
                    break;
                case 2: stndr = "Clase I, Tipo BF: ≥70 MΩ"; //tipo BF
                    break;
                case 3: stndr = "Clase I, Tipo CF: ≥70 MΩ"; //tipo CF
                    break;
            }
        }
        desc_estandar.setText(stndr);
        txt_elemento_eva.setText("Medición de la resistencia del aislamiento entre partes aplicadas y protección de puesta a tierra.");
        //llenando datos si es que la evaluacion ya se realizo:
        if(evaRes3Finished){
            txt_dato.setText(eva_res_3+"");
        }
        //creando popup
        final AlertDialog dialog = builder.create();
        btn_new_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEditTextIsEmpty(txt_dato)){
                    //verificando el dato ingresado:
                    try{
                        //dato ingresado
                        eva_res_3 = Float.parseFloat(txt_dato.getText().toString());
                        //variable bandera para precargar datos si ya han sido ingresados
                        evaRes3Finished = true;
                        //cambia el icono del menu
                        img.setImageResource(R.drawable.seg_elec_2_ok);
                        //cierra ventana
                        dialog.dismiss();
                        //notifica la accion
                        showMsg(context, "¡Inspección de resistencia registrada correctamente!", 1);
                    }
                    catch(NumberFormatException e) {
                        showMsg(context, "El formato del dato ingresado es incorrecto. Verificar dato ingresado.", 0);
                    }
                }
                else{
                    showMsg(context, "Debes evaluar y responder cada elemento de cuestionario", 0);
                }
            }
        });
        //mostrando popup
        dialog.show();
    }


    //8: Medición de la resistencia del aislamiento entre partes aplicadas y partes conductivas sin puesta a tierra
    public void resistencia4(final Context context, int idMedicion, int idClase, int idTipo, final ImageView img){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_resistencia_1,null);
        builder.setView(dialogView);
        //elementos del popup
        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
        TextView desc_estandar = (TextView)dialogView.findViewById(R.id.desc_estandar);
        TextView txt_elemento_eva = (TextView)dialogView.findViewById(R.id.txt_elemento_eva);
        final EditText txt_dato = (EditText) dialogView.findViewById(R.id.txt_dato);
        final Button btn_new_reg = (Button) dialogView.findViewById(R.id.btn_new_reg);
        //definiendo tituto:
        String clase = getClase(idClase);
        String tipo = getTipo(idTipo);

        txt_title.setText("Evaluación de resistencias. Equipo clase: "+clase+", tipo: "+tipo+".");
        //definiendo estandar:
        String stndr = null;
        if(idClase ==1){ //Clase I
            switch (idTipo){
                case 1: stndr = "Clase I, Tipo B: ≥70 MΩ"; //tipo B
                    break;
                case 2: stndr = "Clase I, Tipo BF: ≥70 MΩ"; //tipo BF
                    break;
                case 3: stndr = "Clase I, Tipo CF: ≥70 MΩ"; //tipo CF
                    break;
            }
        }else if(idClase ==2){  //Clase II
            switch (idTipo){
                case 1: stndr = "Clase II, Tipo B: ≥70 MΩ"; //tipo B
                    break;
                case 2: stndr = "Clase II, Tipo BF: ≥70 MΩ"; //tipo BF
                    break;
                case 3: stndr = "Clase II, Tipo CF: ≥70 MΩ"; //tipo CF
                    break;
            }
        }
        desc_estandar.setText(stndr);
        txt_elemento_eva.setText("Medición de la resistencia del aislamiento entre partes aplicadas y partes conductivas sin puesta a tierra.");
        //llenando datos si es que la evaluacion ya se realizo:
        if(evaRes4Finished){
            txt_dato.setText(eva_res_4+"");
        }
        //creando popup
        final AlertDialog dialog = builder.create();
        btn_new_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEditTextIsEmpty(txt_dato)){
                    //verificando el dato ingresado:
                    try{
                        //dato ingresado
                        eva_res_4 = Float.parseFloat(txt_dato.getText().toString());
                        //variable bandera para precargar datos si ya han sido ingresados
                        evaRes4Finished = true;
                        //cambia el icono del menu
                        img.setImageResource(R.drawable.seg_elec_2_ok);
                        //cierra ventana
                        dialog.dismiss();
                        //notifica la accion
                        showMsg(context, "¡Inspección de resistencia registrada correctamente!", 1);
                    }
                    catch(NumberFormatException e) {
                        showMsg(context, "El formato del dato ingresado es incorrecto. Verificar dato ingresado.", 0);
                    }
                }
                else{
                    showMsg(context, "Debes evaluar y responder cada elemento de cuestionario", 0);
                }
            }
        });
        //mostrando popup
        dialog.show();
    }


    //9: Medición de la resistencia del aislamiento entre tensión de alimentación y partes aplicadas
    public void resistencia5(final Context context, int idMedicion, int idClase, int idTipo, final ImageView img){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_resistencia_1,null);
        builder.setView(dialogView);
        //elementos del popup
        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
        TextView desc_estandar = (TextView)dialogView.findViewById(R.id.desc_estandar);
        TextView txt_elemento_eva = (TextView)dialogView.findViewById(R.id.txt_elemento_eva);
        final EditText txt_dato = (EditText) dialogView.findViewById(R.id.txt_dato);
        final Button btn_new_reg = (Button) dialogView.findViewById(R.id.btn_new_reg);
        //definiendo tituto:
        String clase = getClase(idClase);
        String tipo = getTipo(idTipo);

        txt_title.setText("Evaluación de resistencias. Equipo clase: "+clase+", tipo: "+tipo+".");
        //definiendo estandar:
        String stndr = null;
        if(idClase ==1){ //Clase I
            switch (idTipo){
                case 1: stndr = "Clase I, Tipo B: ≥2 MΩ"; //tipo B
                        break;
                case 2: stndr = "Clase I, Tipo BF: ≥70 MΩ"; //tipo BF
                        break;
                case 3: stndr = "Clase I, Tipo CF: ≥70 MΩ"; //tipo CF
                        break;
            }
        }else if(idClase ==2){  //Clase II
            switch (idTipo){
                case 1: stndr = "Clase II, Tipo B: ≥7 MΩ"; //tipo B
                       break;
                case 2: stndr = "Clase II, Tipo BF: ≥70 MΩ"; //tipo BF
                        break;
                case 3: stndr = "Clase II, Tipo CF: ≥70 MΩ"; //tipo CF
                        break;
            }
        }
        desc_estandar.setText(stndr);
        txt_elemento_eva.setText("Medición de la resistencia del aislamiento entre tensión de alimentación y partes aplicadas.");
        //llenando datos si es que la evaluacion ya se realizo:
        if(evaRes5Finished){
            txt_dato.setText(eva_res_5+"");
        }
        //creando popup
        final AlertDialog dialog = builder.create();
        btn_new_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEditTextIsEmpty(txt_dato)){
                    //verificando el dato ingresado:
                    try{
                        //dato ingresado
                        eva_res_5 = Float.parseFloat(txt_dato.getText().toString());
                        //variable bandera para precargar datos si ya han sido ingresados
                        evaRes5Finished = true;
                        //cambia el icono del menu
                        img.setImageResource(R.drawable.seg_elec_2_ok);
                        //cierra ventana
                        dialog.dismiss();
                        //notifica la accion
                        showMsg(context, "¡Inspección de resistencia registrada correctamente!", 1);
                    }
                    catch(NumberFormatException e) {
                        showMsg(context, "El formato del dato ingresado es incorrecto. Verificar dato ingresado.", 0);
                    }
                }
                else{
                    showMsg(context, "Debes evaluar y responder cada elemento de cuestionario", 0);
                }
            }
        });
        //mostrando popup
        dialog.show();
    }



    public String getClase(int idClase){
        switch (idClase){
            case 1: return "I";
            case 2: return "II";
            default: return null;
        }
    }
    public String getTipo(int idTipo){
        switch (idTipo){
            case 1: return "B";
            case 2: return "BF";
            case 3: return "CF";
            default: return null;
        }
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
