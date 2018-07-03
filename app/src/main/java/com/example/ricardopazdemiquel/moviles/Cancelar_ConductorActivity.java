package com.example.ricardopazdemiquel.moviles;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ricardopazdemiquel.moviles.Model.Cancelar;
import com.example.ricardopazdemiquel.moviles.adapter.cancelar_ListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;


public class Cancelar_ConductorActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listView;

    protected void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_marca);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        listView = (ListView) findViewById(R.id.list_vista_marca);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabMarcar);
        fab.setOnClickListener(this);

        //Quitamos barra de notificaciones
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ObtenerMarcas();
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

    @Override
    public void onClick(View v) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        switch (v.getId()) {
            case R.id.fabMarcar:
               // new MarcaDialog().show(fragmentManager, "LoginDialog");
                break;
        }
    }



    private class get_obtener_cancelaciones extends AsyncTask<Void, String, String> {
        private int id;
        public get_obtener_cancelaciones( int id){
            this.id = id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_obtener_cancelaciones");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if (resp.equals("falso")) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                return;
            } else {
                try {
                    JSONArray arr = new JSONArray(resp);
                    ListAdapter adaptador = new cancelar_ListAdapter(Cancelar_ConductorActivity.this, arr);
                    listView.setAdapter(adaptador);

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




    //ListAdapter adaptador  = new Marca_ListAdapter(MarcaActivity.this, lista);
    //listView.setAdapter(adaptador);
}

