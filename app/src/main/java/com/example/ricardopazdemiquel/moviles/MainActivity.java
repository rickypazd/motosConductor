package com.example.ricardopazdemiquel.moviles;



import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.List;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;
import utiles.GPS_service;
import utiles.MapService;
import utiles.Token;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
     {
    private BroadcastReceiver broadcastReceiver;
    private  BroadcastReceiver broadcastReceiverMessage;
        private Button btn_nav_pidesiete;
         private Button btn_nav_formaspago;
         private Button btn_nav_miperfil;
         private Button btn_nav_misviajes;
         private Button btn_nav_preferencias;
         private JSONObject usr_log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else{
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        btn_nav_pidesiete=header.findViewById(R.id.btn_nav_pidesiete);
        btn_nav_pidesiete.setOnClickListener(this);
        btn_nav_formaspago=header.findViewById(R.id.btn_nav_formaspago);
        btn_nav_formaspago.setOnClickListener(this);
        btn_nav_miperfil=header.findViewById(R.id.btn_nav_miperfil);
        btn_nav_miperfil.setOnClickListener(this);
        btn_nav_misviajes=header.findViewById(R.id.btn_nav_misviajes);
        btn_nav_misviajes.setOnClickListener(this);
        btn_nav_preferencias=header.findViewById(R.id.btn_nav_preferencias);
        btn_nav_preferencias.setOnClickListener(this);



        if(getUsr_log()!=null){
            if(!runtime_permissions()){
                seleccionarFragmento("Mapa");
            }
        }
        try {
            new Get_validarCarrera(usr_log.getInt("id")).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //  showDirections(-17.89,-63.1408,-17.6,-63.1408);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(broadcastReceiverMessage == null){
            broadcastReceiverMessage = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                            notificacionReciber(intent);

                }
            };
        }
        registerReceiver(broadcastReceiverMessage,new IntentFilter("Message"));
    }


    //notificacacaiones
    private void notificacionReciber(Intent intent){
            Toast.makeText(this,"sdfsdf",   Toast.LENGTH_LONG).show();
            Intent inte = new Intent(MainActivity.this,Cofirmar_Carrera.class);
            startActivity(inte);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


         // Json : obtiene el id del usuario si es que ya estuvo registrado en la aplicaacion o aiga iniciado sesion
         public JSONObject getUsr_log() {
             SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
             String usr = preferencias.getString("usr_log", "");
             if (usr.length()<=0) {
                 Intent intent = new Intent(MainActivity.this,Login.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
                 return null;
             }else{
                 try {
                     usr_log=new JSONObject(usr);
                     return usr_log;
                 } catch (JSONException e) {
                     e.printStackTrace();
                     return null;
                 }
             }
         }



    private void seleccionarFragmento(String fragmento) {
        Fragment fragmentoGenerico = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Object obj = -1;
        switch (fragmento) {
            case "Mapa":
                fragmentoGenerico= new fr_map();
                break;

        }

            fragmentManager.beginTransaction().replace(R.id.contenmain, fragmentoGenerico).commit();
        if (fragmentoGenerico != null) {
        }
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){

                seleccionarFragmento("Mapa");
            }else {
                runtime_permissions();
            }
        }
    }

    public void showDirections(double lat, double lng, double lat1, double lng1) {

        final Intent intent = new
                Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" +
                "saddr=" + lat + "," + lng + "&daddr=" + lat1 + "," +
                lng1));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);

    }

         @Override
         public void onClick(View v) {
             int id=v.getId();
             switch (id){
                 case R.id.btn_nav_pidesiete:
                     Toast.makeText(this,"sdfsdf",   Toast.LENGTH_LONG).show();
                     break;
                 case R.id.btn_nav_formaspago:
                        selectWaze();
                     break;
                 case R.id.btn_nav_miperfil:
                     break;
                 case R.id.btn_nav_misviajes:
                     break;
                 case R.id.btn_nav_preferencias:
                     break;
             }

             DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
             drawer.closeDrawer(GravityCompat.START);
         }
        public void selectWaze(){
            try
            {
                // Launch Waze to look for Hawaii:
                String url = "https://waze.com/ul?ll=45.6906304,-120.810983&navigate=yes";
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                startActivity( intent );
            }
            catch ( ActivityNotFoundException ex  )
            {
                // If Waze is not installed, open it in Google Play:
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                startActivity(intent);
            }
        }
         private class CargarListaTask extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private double lat;
        private double lon;
        private int id;

        public CargarListaTask(double lat, double lon, int id){
            this.lat=lat;
            this.lon=lon;
            this.id=id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "set_pos_vehiculo");
            parametros.put("lat",lat+"");
            parametros.put("lon",lon+"");
            parametros.put("id_vehiculo",id+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    public class Get_validarCarrera extends AsyncTask<Void, String, String>{
         private int id;
         public Get_validarCarrera(int id){
             this.id=id;
         }

         @Override
         protected void onPreExecute()
         {
             super.onPreExecute();
         }
         @Override
         protected String doInBackground(Void... params) {
             Hashtable<String, String> parametros = new Hashtable<>();
             parametros.put("evento", "get_carrera_cliente");
             parametros.put("id_usr",id+"");
             String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
             return respuesta;
         }
         @Override
         protected void onPostExecute(String resp) {
             super.onPostExecute(resp);
             if (resp.contains("falso")) {
                 Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                 return;
             } else {
                 try {
                     JSONObject obj = new JSONObject(resp);
                     if(obj.getBoolean("exito")) {
                         Intent intent = new Intent(MainActivity.this, EsperandoConductor.class);
                         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                         intent.putExtra("obj_carrera", obj.toString());
                         startActivity(intent);
                     }
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
         }
         @Override
         protected void onProgressUpdate(String... values) {
             super.onProgressUpdate(values);

         }
    }

}
