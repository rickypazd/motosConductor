package com.example.ricardopazdemiquel.moviles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class finalizar_viajeCliente extends AppCompatActivity{



    private Button nombre;
    private RatingBar ratingBar;
    private int id_carrera;
    float calificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_viajecliente);

        id_carrera = Integer.parseInt(getIntent().getStringExtra("id_carrera"));
        nombre = findViewById(R.id.btn_nombre);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calificacion = ratingBar.getRating();
                Toast.makeText (view.getContext(), "Calificación:" + calificacion , Toast .LENGTH_LONG) .show ();
                new finalizar_viajeCliente.Finalizo(id_carrera , calificacion).execute();
            }
        });
    }


    //asyncTask Finalizo carrera
    private class Finalizo extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private int id_carrera;
        private float calificacion;

        public Finalizo(int id_carrera , float finalizo) {
            this.id_carrera = id_carrera;
            this.calificacion = finalizo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(finalizar_viajeCliente.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "");
            parametros.put("id_carrera",id_carrera+"");
            parametros.put("calificacion",calificacion+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
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
            else if(resp.equals("exito")){
                //new MapCarrera.buscar_carrera().execute();
                Intent intent = new Intent(finalizar_viajeCliente.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }



}
