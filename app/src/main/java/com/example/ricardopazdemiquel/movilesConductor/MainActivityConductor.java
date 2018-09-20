package com.example.ricardopazdemiquel.movilesConductor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ricardopazdemiquel.movilesConductor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;
import utiles.MapService;
import utiles.MapService2;
import utiles.Token;

public class MainActivityConductor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver broadcastReceiverMessage;
    private JSONObject obj_turno;
    private JSONObject usr_log;
    private Button btn_nav_pidesiete;
    private Button btn_nav_formaspago;
    private Button btn_nav_miperfil;
    private Button btn_nav_misviajes;
    private Button btn_nav_preferencias;
    private RadioGroup radioGroup;
    private RadioButton activo;
    private RadioButton descativo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_conductor);

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
        activo=findViewById(R.id.activo);
        descativo=findViewById(R.id.desactivo);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main_activity_conductor);
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
         usr_log = getUsr_log();
        if (usr_log == null) {
            Intent intent = new Intent(MainActivityConductor.this, LoginConductor.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            try {
                String carrera= new get_validar_carrera(usr_log.getInt("id")).execute().get();

                if(carrera!=null && carrera.length()>0){
                    JSONObject objcar = new JSONObject(carrera);
                    if(objcar.getBoolean("exito")){
                        if(!runtime_permissions()){
                            Intent i =new Intent(MainActivityConductor.this, MapService2.class);
                            JSONObject turno = new JSONObject(carrera).getJSONObject("turno");
                            i.putExtra("id_vehiculo",turno.getInt("id_vehiculo"));
                            startService(i);
                        }

                    }else{
                        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.remove("carrera");
                        new Get_ActualizarToken(usr_log.getInt("id")).execute();
                        String  as = new get_Turno(usr_log.getInt("id")).execute().get();

                    }
                }else{
                    SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.remove("carrera");
                    new Get_ActualizarToken(usr_log.getInt("id")).execute();
                    String  as = new get_Turno(usr_log.getInt("id")).execute().get();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
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
        registerReceiver(broadcastReceiverMessage,new IntentFilter("confirmar_carrera"));
    }


    Intent inte;
    private void notificacionReciber(Intent intent){
        if(inte==null){
            inte = new Intent(MainActivityConductor.this,Cofirmar_Carrera.class);
            String json  = intent.getStringExtra("json");
            String jsonUsuario  = intent.getStringExtra("jsonUsuario");
            if(json.length()>0){
                inte.putExtra("json",json);
                inte.putExtra("jsonUsuario",jsonUsuario);
                startActivityForResult(inte, 1);

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int result=data.getIntExtra("result",0);
                int tipo=data.getIntExtra("tipo",0);
                if(tipo==2){
                    Intent intent = new Intent(MainActivityConductor.this, MapCarreraTogo.class);
                    intent.putExtra("id_carrera",result);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivityConductor.this, MapCarrera.class);
                    intent.putExtra("id_carrera",result);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                inte=null;
            }
        }
    }//onActivityResult


    // Json : obtiene el id del usuario si es que ya estuvo registrado en la aplicaacion o aiga iniciado sesion
    public JSONObject getUsr_log() {
        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
        String usr = preferencias.getString("usr_log", "");
        if (usr.length() <= 0) {
            return null;
        } else {
            try {
                JSONObject usr_log = new JSONObject(usr);
                return usr_log;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
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
                Intent i =new Intent(getApplicationContext(),MapService2.class);
                startService(i);
            }else {
                runtime_permissions();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_conductor, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btn_nav_pidesiete:
                seleccionarFragmento("carrerasactivas");
                break;
            case R.id.btn_nav_formaspago:
                Intent intent = new Intent(MainActivityConductor.this,Transaccion_cliente_Activity.class);
                startActivity(intent);

                break;
            case R.id.btn_nav_miperfil:
                Intent intenta = new Intent(MainActivityConductor.this,Perfil_Conductor.class);
                startActivity(intenta);
                break;
            case R.id.btn_nav_misviajes:
                seleccionarFragmento("HistorialCarreras");
                break;
            case R.id.btn_nav_preferencias:
                Intent intentas = new Intent(MainActivityConductor.this,Preferencias.class);
                startActivity(intentas);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    private void seleccionarFragmento(String fragmento) {

        Fragment fragmentoGenerico = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Object obj = -1;
        switch (fragmento) {
            case "carrerasactivas":
                fragmentoGenerico= new fragment_carrera_activa();
                break;
            case "HistorialCarreras":
                fragmentoGenerico= new HistorialCarreras();

                break;
        }
        fragmentManager.beginTransaction().replace(R.id.content_conductor, fragmentoGenerico).commit();
        if (fragmentoGenerico != null) {
        }
    }
    private void alert(){

        if(obj_turno!=null){
            activo.setChecked(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityConductor.this);
            builder.setMessage("Esta seguro que desea desactivarse? Usted no recibira viajes.")
                    .setTitle("Terminar Turno")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            // CONFIRM
                            try {
                                new terminar_turno(usr_log.getInt("id")).execute();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            // Create the AlertDialog object and return it
            AlertDialog dialog=builder.create();
            dialog.show();
        }

    }
    public JSONObject getObj_turno() {
        return obj_turno;
    }

    private class get_Turno extends AsyncTask<Void, String, String> {
        private int id;
        public get_Turno( int id){
            this.id=id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_turno_conductor");
            parametros.put("id",id+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if(resp==null){
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }else{
                if(resp.equals("falso")){
                    Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                }else{
                    try {
                        if(resp.length()>0 && !resp.equals("{}")){
                            final JSONObject obj = new JSONObject(resp);
                            obj_turno=obj;
                            if(!runtime_permissions()){
                                Intent i =new Intent(MainActivityConductor.this, MapService2.class);
                                i.putExtra("id_vehiculo",obj_turno.getInt("id_vehiculo"));
                                activo.setChecked(true);
                                descativo.setChecked(false);
                                startService(i);

                            }
                        }else{
                            Log.e(Contexto.APP_TAG, "No tiene turno iniciado.");
                            Intent intent = new Intent(MainActivityConductor.this,InicieTurno.class);
                            descativo.setChecked(true);
                            activo.setChecked(false);
                            startActivity(intent);
                        }
                        radioGroup=findViewById(R.id.group_Activo);
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if(checkedId == R.id.activo){
                                    if(obj_turno==null){
                                        Intent intent = new Intent(MainActivityConductor.this,InicieTurno.class);
                                        descativo.setChecked(true);
                                        activo.setChecked(false);
                                        startActivity(intent);
                                    }

                                }else if(checkedId == R.id.desactivo){
                                    alert();
                                }
                            }
                        });
                        seleccionarFragmento("carrerasactivas");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    private class pushPosition extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private double lat;
        private double lon;
        private int id;

        public pushPosition(double lat, double lon, int id){
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

    public class Get_ActualizarToken extends AsyncTask<Void, String, String>{

        private int id;

        public Get_ActualizarToken(int id){
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
            parametros.put("evento", "actualizar_token");
            parametros.put("id_usr",id+"");
            parametros.put("token", Token.currentToken);
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

    private class get_validar_carrera extends AsyncTask<Void, String, String> {
        private int id;
        public get_validar_carrera( int id){
            this.id=id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {

            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_carrera_conductor");
            parametros.put("id_usr",id+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if (resp == null) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }else{
                if (resp.equals("falso")) {
                    Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                    return;
                } else {
                    try {
                        JSONObject obj = new JSONObject(resp);
                        Boolean bo = obj.getBoolean("exito");
                        if (bo) {
                            if(obj.getInt("estado")==6){
                                Intent intent = new Intent(MainActivityConductor.this, cobranza.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else{
                                if(obj.getInt("id_tipo")==2){
                                    Intent intent = new Intent(MainActivityConductor.this, MapCarreraTogo.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("id_carrera", obj.getInt("id"));
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(MainActivityConductor.this, MapCarrera.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("id_carrera", obj.getInt("id"));
                                    startActivity(intent);
                                }
                            }


                        }else{
                            SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferencias.edit();
                            editor.putString("chat_carrera", new JSONArray().toString());
                            editor.commit();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }


        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    private class terminar_turno extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private int id_usr;

        public terminar_turno(int id_usr) {
            this.id_usr = id_usr;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(MainActivityConductor.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "terminar_turno");
            parametros.put("id",id_usr+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();
            if(resp!=null){
                if(resp.equals("falso")){
                    Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                }else if(resp.equals("exito")){
                    obj_turno=null;
                    descativo.setChecked(true);
                    Intent i =new Intent(MainActivityConductor.this, MapService2.class);
                    stopService(i);
                    seleccionarFragmento("carrerasactivas");
                }
            }

        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }
}
