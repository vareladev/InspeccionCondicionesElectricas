package sv.edu.uca.dei.fies2018v2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import database.Adapter;


public class MainActivity extends AppCompatActivity {

    private EditText login_dui;
    private EditText login_password;
    private Button iniciar_sesion;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_dui = (EditText)findViewById(R.id.text_login_dui);
        login_password = (EditText)findViewById(R.id.text_login_password);
        iniciar_sesion = (Button)findViewById(R.id.btn_iniciar_sesion);
        login_dui.setText("00000000-0");
        login_password.setText("000");

        login_dui.clearFocus();
        login_password.clearFocus();

        //Base de datos
        adapter = new Adapter(this);
        adapter.createDatabase();

        //Listener Boton inicia sesion
        iniciar_sesion.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //verificando usuario
                String dui = login_dui.getText().toString();
                String password = login_password.getText().toString();

                if(dui.length() == 10) {
                    adapter.open();
                    if(adapter.checkUser(dui,password)) {
                        adapter.close();
                        Intent toMenu = new Intent(MainActivity.this, Home.class);
                        MainActivity.this.startActivity(toMenu);
                    }
                    else{
                        adapter.close();
                        login_dui.setText("");
                        login_password.setText("");
                        showMsg(MainActivity.this, "Usuario no encontrado, por favor verifique sus credenciales.",0);
                    }
                }else{
                    login_password.setText("");
                    showMsg(MainActivity.this, "Existe un error con el formato del DUI ingresado, por favor verifique sus credenciales.",0);
                }



            }
        });

        login_dui.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(login_dui.getText().length() == 9) {
                    String duicad = s.toString();
                    if(duicad.indexOf("-")!=-1) {
                        int selectionStart = login_dui.getSelectionStart();
                        //Toast.makeText(MainActivity.this,"posicion="+selectionStart, Toast.LENGTH_LONG).show();
                        String formattedDUI = duicad.replaceAll("[^\\d.]", "");
                        login_dui.setText(formattedDUI);
                        int textLength = login_dui.getText().length();
                        if (selectionStart == 9)
                            login_dui.setSelection(selectionStart-1);
                        else
                            login_dui.setSelection(selectionStart);
                    }
                    else{
                        String formattedDUI = duicad.substring(0,duicad.length()-1)+"-"+duicad.substring(duicad.length()-1,duicad.length());
                        //Toast.makeText(MainActivity.this,"dui="+formattedDUI, Toast.LENGTH_LONG).show();
                        login_dui.setText(formattedDUI);
                        //colocando cursor al final
                        int textLength = login_dui.getText().length();
                        login_dui.setSelection(textLength, textLength);
                    }
                }
            }
        });

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
