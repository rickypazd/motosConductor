package com.example.ricardopazdemiquel.movilesConductor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class Detalle_viaje_Cliente extends AppCompatActivity {

    private TextView nombre;
    private ImageView fotoConductor;
    private TextView placa_numerotelefono;
    private TextView direccion_inicio;
    private TextView direccion_final;
    private TextView fecha;
    private TextView marca_auto;
    private TextView tipo_pago;
    private TextView html_tipos;
    private TextView html_costos;
    private TextView monto_total;
    private Button btn_ver_recorrido;
    private static final int EFECTIVO = 1;
    private static final int CREDITO = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_viaje_cliente);

        Toolbar toolbar = findViewById(R.id.toolbar_detalle_viaje);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nombre = findViewById(R.id.text_nombre);
        fotoConductor = findViewById(R.id.img_perfil_conductor);
        placa_numerotelefono = findViewById(R.id.text_placa_telefono);
        direccion_inicio = findViewById(R.id.text_direccion_inicio);
        direccion_final = findViewById(R.id.text_direccion_fin);
        fecha = findViewById(R.id.text_fecha);
        marca_auto = findViewById(R.id.text_auto_marca);
        tipo_pago = findViewById(R.id.text_tipo_pago);
        html_tipos = findViewById(R.id.text_html_tipos);
        html_costos = findViewById(R.id.text_html_montos);
        btn_ver_recorrido=findViewById(R.id.btn_ver_recorrido);

        Intent intent = getIntent();
        if(intent != null){
            final String id_carrera = intent.getStringExtra("id_carrera");
            new get_viaje_detalle(id_carrera).execute();
            btn_ver_recorrido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verViaje(Integer.parseInt(id_carrera));
                }
            });
        }
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


    public class get_viaje_detalle extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private final String id;

        public get_viaje_detalle(String id_carrera) {
            id = id_carrera;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(Detalle_viaje_Cliente.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_viaje_detalle_conductor");
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
            if (success!="falso"){
                try {
                    if(!success.isEmpty()){
                        JSONObject obj = new JSONObject(success);

                        double latinicial = obj.getDouble("latinicial");
                        double latfinal = obj.getDouble("latfinal");
                        double lnginicial = obj.getDouble("lnginicial");
                        double lngfinal = obj.getDouble("lngfinal");
                        double lat_final_real = obj.getDouble("latfinalreal");
                        double lng_final_real = obj.getDouble("lngfinalreal");
                        String id_carrera = obj.getString("id_carrera");
                        String placa = obj.getString("placa");
                        String telefono="";
                        if (obj.has("status")) {
                            telefono= obj.getString("telefono");
                        }

                        int estado= obj.getInt("estado");
                        int costo = obj.getInt("costo_final");
                        int tipo = obj.getInt("tipo_pago");

                        nombre.setText(obj.getString("nombre"));
                        placa_numerotelefono.setText(placa+" ° "+telefono);
                        fecha.setText(obj.getString("fecha_pedido").substring(0,16));
                        marca_auto.setText(obj.getString("marca")+" "+obj.getString("modelo"));


                        switch (tipo){
                            case(EFECTIVO):
                                tipo_pago.setText("Efectivo");
                                break;
                            case(CREDITO):
                                tipo_pago.setText("Credito");
                                break;
                        }
                        JSONArray array = obj.getJSONArray("detalle_costo");
                        JSONObject object;
                        String html_detalle ="";
                        String html_costo="";
                        double auxCosto;
                        for (int i = 0; i < array.length(); i++) {
                            object = array.getJSONObject(i);
                            auxCosto = Double.parseDouble(object.getString("costo"));
                            html_detalle += "<p>"+object.getString("nombre")+"</p>";
                            html_costo += "<p>"+String.format("%.2f", auxCosto) +" Bs.</p>";
                        }
                        html_detalle += "<p>Total</p>";

                        if(get_estado(estado)){
                            direccion_inicio.setText(getCompleteAddressString(latinicial,lnginicial));
                            direccion_final.setText(getCompleteAddressString(lat_final_real,lng_final_real));
                            html_costo += "<p>"+costo+" Bs.</p>";
                        }else if(!get_estado(estado)){
                            direccion_inicio.setText(getCompleteAddressString(latinicial,lnginicial));
                            direccion_final.setText(getCompleteAddressString(latfinal,lngfinal));
                            html_costo += "<p>0 Bs.</p>";
                        }

                        html_tipos.setText(Html.fromHtml(html_detalle),TextView.BufferType.SPANNABLE);
                        html_costos.setText(Html.fromHtml(html_costo),TextView.BufferType.SPANNABLE);
                    }else{
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                return;
            }
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(Detalle_viaje_Cliente.this, Locale.getDefault());
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

    private Boolean get_estado(int valor){
        switch (valor) {
            case 7:
                return true;
            case 10:
                return false;
        }
        return null;
    }
    private void verViaje(int id){
        Intent intent = new Intent(Detalle_viaje_Cliente.this,PerfilCarrera.class);
        intent.putExtra("id_carrera",id);
        startActivity(intent);
    }
}
