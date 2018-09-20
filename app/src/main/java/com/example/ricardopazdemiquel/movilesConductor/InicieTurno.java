package com.example.ricardopazdemiquel.movilesConductor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ricardopazdemiquel.movilesConductor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;
import utiles.Font;
import utiles.Token;

public class InicieTurno extends AppCompatActivity {

    private ListView lista_vehiculos;
    JSONObject usr_log;
    private CardView btn_iniciar_sin_vehiculo;
    private CardView linear_iniciar_sin_vehi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicie_turno);

        lista_vehiculos=findViewById(R.id.lv_vehiculos);
        btn_iniciar_sin_vehiculo= findViewById(R.id.iniciar_como_super_7);
        linear_iniciar_sin_vehi=findViewById(R.id.linear_iniciar_sin_vehi);
       // TextView text_title = findViewById(R.id.tv);
        //Font.Gotham_Rounded.apply(this,text_title);
         usr_log = getUsr_log();
        if (usr_log == null) {
            Intent intent = new Intent(InicieTurno.this, LoginConductor.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        try {
            new is_act_super(usr_log.getInt("id")).execute();
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
    private class is_act_super extends AsyncTask<Void, String, String> {
        private int id;
        public is_act_super( int id){

            this.id=id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "is_act_super");
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
                JSONObject obj = new JSONObject(resp);
                if(obj.length()>0){
                    if(obj.getBoolean("exito")){
                        if(obj.getBoolean("act_supe")){
                            linear_iniciar_sin_vehi.setVisibility(View.VISIBLE);
                            linear_iniciar_sin_vehi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //INICIAR TURNO SIN VEHICULO
                                    try {
                                        new iniciar_turno(usr_log.getInt("id")).execute();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class iniciar_turno extends AsyncTask<Void, String, String> {
        private ProgressDialog progreso;
        private int id_conductor;
        public iniciar_turno(int id_conductor){
            this.id_conductor=id_conductor;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "iniciar_turno_sin_vehiculo");
            parametros.put("id_cond",id_conductor+"");
            parametros.put("token", Token.currentToken);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if(resp!=null){
                if(resp.equals("falso")){
                    Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                }
                if(resp.equals("exito")){
                    Intent intent = new Intent(InicieTurno.this, MainActivityConductor.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    Log.e(Contexto.APP_TAG, "Hubo un error al registrar turno");
                }
            }

        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

    }
}
