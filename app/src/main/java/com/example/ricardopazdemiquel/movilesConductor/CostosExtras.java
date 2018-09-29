package com.example.ricardopazdemiquel.movilesConductor;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ricardopazdemiquel.movilesConductor.adapter.costos_extrasAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;

public class CostosExtras extends AppCompatActivity {
    private String id_carrera;
    private ListView list_costos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costos_extras);
        id_carrera=getIntent().getStringExtra("id_carrera");
        list_costos=findViewById(R.id.list_costos);
        new cargar_lista().execute();

    }
    private class cargar_lista extends AsyncTask<Void, String, String> {
        private ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progreso = new ProgressDialog(CostosExtras.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("obteniendo datos");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","get_costos_extras_estado");
            param.put("id_carrera",id_carrera+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();
            if (resp == null) {
                Toast.makeText(CostosExtras.this,"Error al obtener datos.", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    JSONArray arr = new JSONArray(resp);
                    costos_extrasAdapter adapter = new costos_extrasAdapter(CostosExtras.this,arr,id_carrera);
                    list_costos.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progreso.setMessage(values[0]);
        }

    }
}
