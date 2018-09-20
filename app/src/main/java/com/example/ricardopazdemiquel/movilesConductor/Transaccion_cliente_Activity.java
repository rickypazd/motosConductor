package com.example.ricardopazdemiquel.movilesConductor;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.example.ricardopazdemiquel.movilesConductor.adapter.Adapter_transaccion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class Transaccion_cliente_Activity extends AppCompatActivity {

    private static final String TAG ="fragment_explorar";
    private ListView lv;

    @Override
    protected void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_list_transaccion);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = findViewById(R.id.list_transaccion);

        final JSONObject usr_log = getUsr_log();
        if (usr_log != null) {
            try {
                new get_transacciones(usr_log.getString("id")).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
    public void onResume() {
        super.onResume();
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

    public class get_transacciones extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private final String id;
        get_transacciones(String id_usr) {
            id = id_usr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(Transaccion_cliente_Activity.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_transacciones_id");
            parametros.put("id",id);
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
            if (success != null || !success.isEmpty()){
                try {
                    JSONArray jsonArray = new JSONArray(success);
                    Adapter_transaccion adaptador_mis_viajes = new Adapter_transaccion(Transaccion_cliente_Activity.this,jsonArray);
                    lv.setAdapter(adaptador_mis_viajes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(Transaccion_cliente_Activity.this,"Error al obtener Datos", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

}
