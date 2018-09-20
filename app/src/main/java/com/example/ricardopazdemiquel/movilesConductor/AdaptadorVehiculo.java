package com.example.ricardopazdemiquel.movilesConductor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ricardopazdemiquel.movilesConductor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;
import utiles.Token;

public class AdaptadorVehiculo extends BaseAdapter {

    private JSONArray listaVehiculos;
    private Context contexto;
    private int id_conductor;
    public AdaptadorVehiculo(Context contexto, JSONArray lista,int id_con) {
        this.contexto = contexto;
        this.listaVehiculos = lista;
        this.id_conductor=id_con;
    }

    @Override
    public int getCount() {
        return listaVehiculos.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return listaVehiculos.get(i);
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
                    .inflate(R.layout.layout_vehiculo, viewGroup, false);
        }


        TextView tv_placa = view.findViewById(R.id.tv_placa);
        TextView tv_marca = view.findViewById(R.id.tv_marca);
        TextView tv_modelo = view.findViewById(R.id.tv_modelo);
        TextView tv_ano = view.findViewById(R.id.tv_ano);
        TextView tv_color = view.findViewById(R.id.tv_color);


        CardView inciar = view.findViewById(R.id.btn_inciar);
        try {
            final JSONObject cancha = listaVehiculos.getJSONObject(i);
            //imgCancha.setImageResource(cancha.getImagen());

            tv_placa.setText("Placa: "+cancha.getString("placa"));
            tv_marca.setText("Marca: "+cancha.getString("marca"));
            tv_modelo.setText("Tipo: "+cancha.getString("modelo"));
            tv_ano.setText("Modelo: "+cancha.getString("ano"));
            tv_color.setText("Color: "+cancha.getString("color"));

            inciar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new iniciar_turno(cancha.getInt("id_vehiculo"),id_conductor).execute();
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
    public class AsyncTaskLoadImage  extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "AsyncTaskLoadImage";
        private ImageView imageView;
        public AsyncTaskLoadImage(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
    private class iniciar_turno extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private int id;
        private int id_conductor;

        public iniciar_turno( int id,int id_conductor){
            this.id_conductor=id_conductor;
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
            parametros.put("evento", "iniciar_turno");
            parametros.put("id",id+"");
            parametros.put("id_cond",id_conductor+"");
            parametros.put("token", Token.currentToken);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(contexto.getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if(resp!=null){
                if(resp.equals("falso")){
                    Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                    return;
                }
                if(resp.equals("exito")){
                    Intent intent = new Intent(contexto, MainActivityConductor.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    contexto.startActivity(intent);
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
