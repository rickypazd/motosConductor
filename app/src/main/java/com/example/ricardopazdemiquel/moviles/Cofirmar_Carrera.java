package com.example.ricardopazdemiquel.moviles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class Cofirmar_Carrera extends AppCompatActivity {
    private Button btn_aceptar;
    private boolean acepto;
    private int id_carrera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cofirmar__carrera);
        btn_aceptar=findViewById(R.id.btn_aceptar_Carrera);
        acepto=false;
        id_carrera = Integer.parseInt(getIntent().getStringExtra("id_carrera"));
        if(id_carrera<=0){
            finish();
        }
        final JSONObject usr_log = getUsr_log();
        if (usr_log == null) {
            Intent intent = new Intent(Cofirmar_Carrera.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            btn_aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acepto=true;
                    try {
                        new confirmar_carrera(id_carrera,usr_log.getInt("id")).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!acepto){
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();

                    }

                }
            }, 9000);
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
    private class confirmar_carrera extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private int id_carrera;
        private int id_usuairo;

        public confirmar_carrera(int id_carrera, int id_usuairo) {
            this.id_carrera = id_carrera;
            this.id_usuairo = id_usuairo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(Cofirmar_Carrera.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "confirmar_carrera");
            parametros.put("id_carrera",id_carrera+"");
            parametros.put("id_conductor",id_usuairo+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();

            if(resp.equals("falso")){
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                return;
            }
            if(resp.equals("exito")){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",id_carrera);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

    }
}
