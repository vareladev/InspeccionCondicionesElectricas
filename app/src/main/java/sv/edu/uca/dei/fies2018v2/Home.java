package sv.edu.uca.dei.fies2018v2;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentManager;
//import android.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

public class Home extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //cargando fragmento Menu
        FragmenMenu fm = new FragmenMenu();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, fm).commit();
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            FragmentManager.BackStackEntry bse = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1);
            bse.getName();

            if(bse.getName() == "fragmentMedMenuMed"){
                while (fragmentManager.getBackStackEntryCount() > 0){
                    fragmentManager.popBackStackImmediate();
                }
            }
            else if(bse.getName() == "seguridadElectricaToma"){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("¿Salir de esta inspección?");
                builder.setMessage("Advertencia: Todos los datos registrados hasta el momento se perderán.");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_warning);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        while (fragmentManager.getBackStackEntryCount() > 0){
                            fragmentManager.popBackStackImmediate();
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();//showing the dialog
            }
            else{
                fragmentManager.popBackStack();
            }
        } else {
            Log.i("MainActivity", "se acabaron los back de fragmentos atras y llegamos a login");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea cerrar sesión?");
            builder.setCancelable(false);
            builder.setIcon(R.drawable.ic_warning);
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //dialog.cancel();
                    Home.super.onBackPressed();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();//showing the dialog

        }
    }

    public void openMainMenu(){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            FragmentManager.BackStackEntry bse = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1);
            while (fragmentManager.getBackStackEntryCount() > 0){
                fragmentManager.popBackStackImmediate();
            }
        }
    }

    public void openFragmentMeasureDetails(int idMedicion, String fechaMedicion){
        setTitle("Administración de inspecciones de seguridad");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, Fragment_measures_history.newInstance(idMedicion, fechaMedicion))
                .addToBackStack("fragment measure history")
                .commit();
        return;
    }

    public void openFragmentHistoryDetails(int idMedicion, int idHospital, int idArea){
        setTitle("Administración de inspecciones de seguridad");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, fragment_details_history.newInstance(idMedicion, idHospital, idArea))
                .addToBackStack("fragment detail history")
                .commit();
        return;
    }

    public void openFragmentHistory(){
        setTitle("Administración de inspecciones de seguridad");

        Fragment_history_med fragment_history_med = new Fragment_history_med();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, fragment_history_med)
                .addToBackStack("fragmentInspeccion")
                .commit();
        return;
    }

    public void openFragmentEquipo(){
        setTitle("Administración de equipo");

        AdministrarEquipo administrarEquipo = new AdministrarEquipo();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, administrarEquipo)
                .addToBackStack("equipFromMenu")
                .commit();
        return;
    }

    /*public void openFragNewMed(){
        setTitle("Inspección de instalaciones electricas y condiciones ambientales");

        MenuMediciones menuMediciones = new MenuMediciones();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, menuMediciones)
                .addToBackStack("equipFromMenu")
                .commit();
        return;
    }*/

    public void openFragNewMed(ArrayList<String> idEquipList){
        setTitle("Nueva medición.");

        MenuMediciones menuMediciones = new MenuMediciones();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, MenuMediciones.newInstance(idEquipList))
                .addToBackStack("fragment detail history")
                .commit();
        return;
    }



    public void openFragSegElec(ArrayList<String> idEquipList){
        setTitle("Inspección de seguridad eléctrica");

        //MenuMediciones menuMediciones = new MenuMediciones();
        Fragment_Seg_Elec_1 fragment_Seg_Elec_1 = new Fragment_Seg_Elec_1();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, Fragment_Seg_Elec_1.newInstance(idEquipList))
                .addToBackStack("create new register")
                .commit();
        return;
    }


    public void openVarRegMed(){
        setTitle("Inspección de instalaciones electricas y condiciones ambientales");

        FragmentMedMenu fragmentMedMenu = new FragmentMedMenu();
        //MenuMediciones menuMediciones = new MenuMediciones();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, fragmentMedMenu)
                .addToBackStack("fragmentMedMenuMed")
                .commit();
        return;
    }

    public void openFragc1tB(int idSegElec, int clase, int tipo){
        setTitle("Inspección de seguridad eléctrica equipo clase 1 tipo B");
        FragmentClase1Tipo1 fragmentClase1Tipo1 = new FragmentClase1Tipo1();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, fragmentClase1Tipo1.newInstance(idSegElec, clase, tipo))
                .addToBackStack("seguridadElectricaToma")
                .commit();
        return;
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

    public void msj_cerrarsesion(Context context, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setCancelable(false)
                .setIcon(R.drawable.ic_warning)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();//showing the dialog
    }
}
