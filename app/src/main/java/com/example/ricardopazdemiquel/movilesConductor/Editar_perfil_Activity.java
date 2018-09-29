package com.example.ricardopazdemiquel.movilesConductor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class Editar_perfil_Activity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG ="fragment_explorar";
    private static final String NOMBRE_USUARIO ="nombre_usuario";
    private static final String APELLIDO_USUARIO ="apellido_usuario";
    private static final String TELEFONO_USUARIO ="telefono_usuario";
    private static final String CORREO_USUARIO ="correo_usuario";
    private static final String CONTRASEÑA_USUARIO ="contraseña_usuario";

    private TextView textNombre;
    private TextView text_titulo;
    private Button btn_guardar;
    private String tipo;
    private TextView text_apellido_ma;
    private LinearLayout Liner_apellido;

    @Override
    protected void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textNombre = findViewById(R.id.text_nombreCliente);
        text_titulo = findViewById(R.id.text_titulo);
        btn_guardar  = findViewById(R.id.btn_guardar);
        text_apellido_ma = findViewById(R.id.text_apellido_ma);
        Liner_apellido = findViewById(R.id.Liner_apellido);
        btn_guardar.setOnClickListener(this);

        Intent intent= getIntent();
        tipo = intent.getStringExtra("tipo");
        switch (tipo){
            case NOMBRE_USUARIO:
                String nombre = intent.getStringExtra("nombre");
                textNombre.setText(nombre);
                break;
            case APELLIDO_USUARIO:
                String apellido_pa = intent.getStringExtra("apellido_pa");
                String apellido_ma = intent.getStringExtra("apellido_ma");
                text_titulo.setText("Apellido paterno");
                Liner_apellido.setVisibility(View.VISIBLE);
                textNombre.setText(apellido_pa +" "+ apellido_ma);
                break;
            case TELEFONO_USUARIO:
                String telefono = intent.getStringExtra("telefono");
                text_titulo.setText("Teléfono");
                textNombre.setText(telefono);
                break;
            case CORREO_USUARIO:
                String correo = intent.getStringExtra("correo");
                text_titulo.setText("Correo electrónico");
                textNombre.setText(correo);
                break;
            case CONTRASEÑA_USUARIO:
                text_titulo.setText("Ingrese una contraseña nueva");
                text_titulo.setTextSize(16);
                btn_guardar.setText("Actualizar Contraseña");
                break;
        }

    }

    // Opcion para ir atras sin reiniciar el la actividad anterior de nuevo
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }


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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_guardar:
                isValue();
            break;
        }
    }

    private void isValue() {
        final JSONObject usr_log = getUsr_log();
        String text_usuario = textNombre.getText().toString().trim();
        boolean isValid = true;
        if (text_usuario.isEmpty()) {
            textNombre.setError("Campo obligatorio");
            isValid = false;
        }
        if (!isValid) {
            return;
        }
        switch (tipo) {
            case NOMBRE_USUARIO:
                if (usr_log != null) {
                    try {
                        String id = usr_log.getString("id");
                        String apellido_pa = usr_log.getString("apellido_pa");
                        String apellido_ma = usr_log.getString("apellido_ma");
                        String telefono = usr_log.getString("telefono");
                        String correo = usr_log.getString("correo");
                        new edit_perfil_usuario(id, text_usuario, apellido_pa, apellido_ma, telefono, correo).execute();
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            case APELLIDO_USUARIO:
                String apellido_materno = text_apellido_ma.getText().toString().trim();
                if (usr_log != null) {
                    try {
                        String id = usr_log.getString("id");
                        String nombre = usr_log.getString("nombre");
                        String telefono = usr_log.getString("telefono");
                        String correo = usr_log.getString("correo");
                        new edit_perfil_usuario(id, nombre, text_usuario, apellido_materno , telefono, correo).execute();
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            case TELEFONO_USUARIO:
                if (usr_log != null) {
                    try {
                        String id = usr_log.getString("id");
                        String nombre = usr_log.getString("nombre");
                        String apellido_pa = usr_log.getString("apellido_pa");
                        String apellido_ma = usr_log.getString("apellido_ma");
                        String correo = usr_log.getString("correo");
                        new edit_perfil_usuario(id, nombre, apellido_pa, apellido_ma, text_usuario, correo).execute();
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            case CORREO_USUARIO:
                if (usr_log != null) {
                    try {
                        String id = usr_log.getString("id");
                        String nombre = usr_log.getString("nombre");
                        String apellido_pa = usr_log.getString("apellido_pa");
                        String apellido_ma = usr_log.getString("apellido_ma");
                        String telefono = usr_log.getString("telefono");
                        new edit_perfil_usuario(id , nombre, apellido_pa, apellido_ma, telefono, text_usuario).execute();
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            case CONTRASEÑA_USUARIO:
                if (usr_log != null) {
                    try {
                        String id = usr_log.getString("id");
                        String apellido_pa = usr_log.getString("apellido_pa");
                        String apellido_ma = usr_log.getString("apellido_ma");
                        String telefono = usr_log.getString("telefono");
                        String correo = usr_log.getString("correo");
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    public class edit_perfil_usuario extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;

        private String id;
        private String nombre;
        private String apellido_pa;
        private String apellido_ma;
        private String telefono;
        private String correo;

        edit_perfil_usuario(String id_usr , String nombre,String apellido_pa,String apellido_ma, String telefono, String correo) {
            this.id = id_usr;
            this.nombre = nombre;
            this.apellido_pa = apellido_pa;
            this.apellido_ma = apellido_ma;
            this.telefono = telefono;
            this.correo = correo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(Editar_perfil_Activity.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "editar_perfil_cliente");
            parametros.put("id_usuario",id);
            parametros.put("nombre",nombre);
            parametros.put("apellido_pa",apellido_pa);
            parametros.put("apellido_ma",apellido_ma);
            parametros.put("telefono",telefono);
            parametros.put("correo",correo);
            String respuesta ="";
            try {
                respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            } catch (Exception ex) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(final String success) {
            super.onPostExecute(success);
            progreso.dismiss();
            if (success == null){
                Toast.makeText(Editar_perfil_Activity.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            } else if (!success.isEmpty()){
                try {
                    JSONObject usr = new JSONObject(success);
                    if(usr.getString("exito").equals("si")){
                        finish();
                    }else{
                        Toast.makeText(Editar_perfil_Activity.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(Editar_perfil_Activity.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    public class edit_contraseña extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;

        private String id;
        private String nombre;
        private String apellido_pa;
        private String apellido_ma;
        private String telefono;
        private String correo;

        edit_contraseña(String id_usr , String nombre,String apellido_pa,String apellido_ma, String telefono, String correo) {
            this.id = id_usr;
            this.nombre = nombre;
            this.apellido_pa = apellido_pa;
            this.apellido_ma = apellido_ma;
            this.telefono = telefono;
            this.correo = correo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(Editar_perfil_Activity.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "fdgdfgdf");
            parametros.put("id_usuario",id);
            parametros.put("nombre",nombre);
            parametros.put("apellido_pa",apellido_pa);
            parametros.put("apellido_ma",apellido_ma);
            parametros.put("telefono",telefono);
            parametros.put("correo",correo);
            String respuesta ="";
            try {
                respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            } catch (Exception ex) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(final String success) {
            super.onPostExecute(success);
            progreso.dismiss();
            if (success == null){
                Toast.makeText(Editar_perfil_Activity.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }else if (!success.isEmpty()){
                try {
                    JSONObject usr = new JSONObject(success);
                    if(usr.getString("exito").equals("si")){
                        finish();
                    }else{
                        Toast.makeText(Editar_perfil_Activity.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                        Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(Editar_perfil_Activity.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

}
