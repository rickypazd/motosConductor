package com.example.ricardopazdemiquel.moviles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class Cofirmar_Carrera extends AppCompatActivity {

    private Button btn_aceptar;
    private Button btn_rechazar;
    private EditText nombre;
    private EditText inicio;
    private EditText llegada;
    private EditText puntuacion;
    private boolean acepto;
    private int id_carrera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cofirmar__carrera);

        btn_aceptar=findViewById(R.id.btn_aceptar_Carrera);

        nombre =findViewById(R.id.editName);
        inicio =findViewById(R.id.editInicio);
        llegada =findViewById(R.id.editLlegada);
        puntuacion =findViewById(R.id.editaPuntacion);

        acepto=false;

        JSONObject jsonUsuario;
        JSONObject json;
        try {
            jsonUsuario = new JSONObject(getIntent().getStringExtra("jsonUsuario"));
            json = new JSONObject(getIntent().getStringExtra("json"));

            String auxNombre = jsonUsuario.getString("nombre");
            String auxapellidopa = jsonUsuario.getString("apellido_pa");
            String auxapellidoma = jsonUsuario.getString("apellido_ma");
            double latinicio = json.getDouble("latinicial");
            double lnginicio = json.getDouble("lnginicial");
            double latfinal = json.getDouble("latfinal");
            double lngfinal = json.getDouble("lngfinal");

            nombre.setText(auxNombre +" "+ auxapellidopa +" "+ auxapellidoma);
            puntuacion.setText("6.9");
            inicio.setText(getCompleteAddressString(latinicio,lnginicio));
            llegada.setText(getCompleteAddressString(latfinal,lngfinal));

            id_carrera = json.getInt("id");

        } catch (JSONException e) {
            e.printStackTrace();
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

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction addr", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction addr", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction addr", "Canont get Address!");
        }
        return strAdd;
    }

}
