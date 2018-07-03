package com.example.ricardopazdemiquel.moviles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;
import utiles.Token;

public class InicieTurno extends AppCompatActivity {

    private ListView lista_vehiculos;
    JSONObject usr_log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicie_turno);

        lista_vehiculos=findViewById(R.id.lv_vehiculos);
         usr_log = getUsr_log();
        if (usr_log == null) {
            Intent intent = new Intent(InicieTurno.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        try {
            new get_vehiculos_disponibles(usr_log.getInt("id")).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    private class get_vehiculos_disponibles extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private int id;

        public get_vehiculos_disponibles( int id){

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
            parametros.put("evento", "get_vehiculo_disponible_con");
            parametros.put("id",id+"");

            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if(resp.equals("falso")){
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                return;
            }
            try {
                JSONArray obj = new JSONArray(resp);
                if(obj.length()>0){
                    AdaptadorVehiculo adaptadorVehiculo = new AdaptadorVehiculo(InicieTurno.this,obj,usr_log.getInt("id"));
                    lista_vehiculos.setAdapter(adaptadorVehiculo);
                }else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

    }
}
