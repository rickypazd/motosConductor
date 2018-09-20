package com.example.ricardopazdemiquel.movilesConductor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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


public class cobranza extends AppCompatActivity{

    private TextView tv_total;
    private TextView tv_metodo_pago;
    private TextView tv1;
    private TextView tv2;

    private Button btn_cobranza;
    String carrera;
    private JSONObject obj_carrera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobranza);

        tv_total = findViewById(R.id.tv_total);
        btn_cobranza = findViewById(R.id.btn_cobrar);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv_metodo_pago=findViewById(R.id.tv3);
        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
        carrera = preferencias.getString("carrera", "");

        if(carrera.length()>0){
            try {
                obj_carrera = new JSONObject(carrera);

                tv_total.setText(obj_carrera.getInt("costo_final")+" Bs.");
                int metodopago=obj_carrera.getInt("tipo_pago");
                if(metodopago==1){
                    tv_metodo_pago.setText("Efectivo");
                }else if(metodopago==2){
                    tv_metodo_pago.setText("Creditos 7");
                }
                JSONArray detalle = obj_carrera.getJSONArray("detalle_costo");
                JSONObject obj;
                String info="";
                String costo="";
                for (int i = 0; i < detalle.length(); i++) {
                    obj=detalle.getJSONObject(i);
                    switch (obj.getInt("tipo")){
                        case 1:
                            info+="<p>Costo x/Distancia: </p></br>";
                            break;
                        case 2:
                            info+="<p>Costo x/Tiempo: </p></br>";
                            break;
                        case 3:
                            info+="<p>Costo Basico: </p></br>";
                            break;
                        case 4:
                            info+="<p>Pago de Deuda: </p></br>";
                            break;
                        case 5:
                            info+="<p>"+obj.getString("nombre")+"</p></br>";
                            break;
                    }
                    costo+="<p>"+String.format("%.2f", obj.getDouble("costo"))+"</p></br>";
                }
                tv1.setText(Html.fromHtml(info));
                tv2.setText(Html.fromHtml(costo));
                btn_cobranza.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            new Cobrar_Carrera(obj_carrera.getInt("id")).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }else{
        }
    }

    //asyncTask Cobrar carrera
    private class Cobrar_Carrera extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private int id_carrera;

        public Cobrar_Carrera(int id_carrera) {
            this.id_carrera = id_carrera;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(cobranza.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "cobrar_carrera");
            parametros.put("id_carrera",id_carrera+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();
            if(resp!=null){
            if(resp.equals("falso")){
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                return;
            }
            else if(resp.equals("exito")){
                //new MapCarrera.buscar_carrera().execute();
                SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                SharedPreferences.Editor  edit = preferencias.edit();
                edit.remove("carrera");

                Intent intent = new Intent(cobranza.this, MainActivityConductor.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

}

