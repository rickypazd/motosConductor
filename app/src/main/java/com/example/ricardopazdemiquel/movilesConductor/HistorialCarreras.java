package com.example.ricardopazdemiquel.movilesConductor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ricardopazdemiquel.movilesConductor.adapter.Adaptador_mis_viajes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class HistorialCarreras extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private ListView lv;
    public HistorialCarreras() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_historial_carreras, container, false);
       // verViaje(93);
        lv = view.findViewById(R.id.list_misViajes);
        final JSONObject usr_log = getUsr_log();
        if (usr_log != null) {
            try {
                new User_getPerfil(usr_log.getString("id")).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
    private void verViaje(int id){
        Intent intent = new Intent(getActivity(),PerfilCarrera.class);
        intent.putExtra("id_carrera",id);
        startActivity(intent);
    }
    public JSONObject getUsr_log() {
        SharedPreferences preferencias = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
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
    public class User_getPerfil extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private final String id;
        User_getPerfil(String id_usr) {
            id = id_usr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(getActivity());
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_mis_viajes_conductor");
            parametros.put("id",id);
            String respuesta ="";
            try {
                respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            } catch (Exception ex) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(final String success) {
            super.onPostExecute(success);
            progreso.dismiss();
            if(success!=null){
                if (!success.isEmpty()){
                    try {
                        JSONArray jsonArray = new JSONArray(success);
                        Adaptador_mis_viajes mis_viajes= new Adaptador_mis_viajes(getActivity(),jsonArray);
                        lv.setAdapter(mis_viajes);
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

}
