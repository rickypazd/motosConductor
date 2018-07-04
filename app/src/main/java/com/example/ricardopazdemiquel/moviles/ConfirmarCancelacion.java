package com.example.ricardopazdemiquel.moviles;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

public class ConfirmarCancelacion extends AppCompatActivity {

    private JSONObject obj_cancelacion;
    private TextView tv_cancelacion_mensaje;
    private Button btn_confirmar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_cancelacion);
        tv_cancelacion_mensaje=findViewById(R.id.tv_cancelacion_mensaje);
        btn_confirmar=findViewById(R.id.btn_confirmar);
        String resp = getIntent().getStringExtra("obj_cancelacion");
        try {
            obj_cancelacion=new JSONObject(resp);
            // CONTENDIO DEL JSON  exito boolean, fecha_cancelacion, id_carrera, id_tipo, tipo_cancelacion,id_usr,total,cobro
            if(obj_cancelacion.getBoolean("cobro")){
                tv_cancelacion_mensaje.setText("Cancelar la carrera en este punto tiene un costo de: "+obj_cancelacion.getDouble("total")+" Bs.");
            }else{
                tv_cancelacion_mensaje.setText("Cancelar la carrera en este punto no tiene costo.");
            }

            btn_confirmar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new cancelar_carrera(obj_cancelacion.toString()).execute();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private class cancelar_carrera extends AsyncTask<Void, String, String> {

        private String json;

        public cancelar_carrera( String json){
            this.json = json;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "ok_cancelar_carrera");
            parametros.put("json",json+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if (resp.equals("falso")) {
                Log.e(Contexto.APP_TAG, "Hubo un error al obtener la lista de servidor.");
                return;
            } else {

                    if(resp.equals("exito")){
                        Intent intent = new Intent(ConfirmarCancelacion.this, MainActivityConductor.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

}
