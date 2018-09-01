package com.example.ricardopazdemiquel.movilesConductor.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardopazdemiquel.movilesConductor.ConfirmarCancelacion;
import com.example.ricardopazdemiquel.movilesConductor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

import static android.content.Context.MODE_PRIVATE;

public class costos_extrasAdapter extends BaseAdapter {

    private JSONArray listaCanchas;
    private Context contexto;


    private  String id_carrera;


    public costos_extrasAdapter(Context contexto, JSONArray lista , String id_carrera) {
        this.contexto = contexto;
        this.listaCanchas = lista;
        this.id_carrera = id_carrera;
    }

    @Override
    public int getCount() {
        return listaCanchas.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return listaCanchas.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(contexto)
                    .inflate(R.layout.costos_extras, viewGroup, false);
        }
        TextView nombre = view.findViewById(R.id.Cnombre);
        TextView costo = view.findViewById(R.id.Ccosto);
        CheckBox estado =view.findViewById(R.id.Cestado);
        try {
            final JSONObject cancha = listaCanchas.getJSONObject(i);
            nombre.setText(cancha.getString("nombre"));
            costo.setText(cancha.getString("costo"));
            estado.setChecked(cancha.getBoolean("estado"));
            estado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    try {
                        new marcar(cancha.getInt("id"),id_carrera).execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }



    private class marcar extends AsyncTask<Void, String, String> {

        private int id_costo;
        private String id_carrera;

        public marcar( int id_costo , String id_carrera){
            this.id_costo = id_costo;
            this.id_carrera = id_carrera;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "marcar_costo_extra");
            parametros.put("id_costo",id_costo+"");
            parametros.put("id_carrera",id_carrera);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(contexto.getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if(resp!=null) {
                if (resp.equals("falso")) {
                    Log.e(Contexto.APP_TAG, "Hubo un error al obtener la lista de servidor.");
                    return;
                } else {
                    Toast.makeText(contexto,"marcado",Toast.LENGTH_LONG).show();
                }
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }



}
