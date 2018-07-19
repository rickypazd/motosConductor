package com.example.ricardopazdemiquel.moviles;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;


import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Constants;
import utiles.DirectionsJSONParser;

public class EsperandoConductor extends AppCompatActivity {
    MapView mMapView;
    private GoogleMap googleMap;
    JSONObject json_carrera;
    private LinearLayout ll_conductor_llego;
    private LinearLayout perfil_condutor;

    //añadiendo los broadcaast
    private BroadcastReceiver broadcastReceiverMessage;
    private BroadcastReceiver broadcastReceiverMessageconductor;
    private BroadcastReceiver broadcastReceiverInicioCarrera;
    private BroadcastReceiver broadcastReceiverFinalizoCarrera;
    private BroadcastReceiver broadcastReceiverCanceloCarrera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esperando_conductor);
        try {
            json_carrera=new JSONObject(getIntent().getStringExtra("obj_carrera"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ll_conductor_llego=findViewById(R.id.ll_condctor);
        perfil_condutor=findViewById(R.id.perfil_conductor);

        mMapView=findViewById(R.id.mapView2);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        MapsInitializer.initialize(this.getApplicationContext());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                //mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_map)));
                if (ActivityCompat.checkSelfPermission(EsperandoConductor.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EsperandoConductor.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mMap.setMyLocationEnabled(true);
                if(json_carrera!=null){
                    hilo();
                }

            }
        });

        if (mMapView != null &&
                mMapView.findViewById(Integer.parseInt("1")) != null) {
            ImageView locationButton = (ImageView) ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 600);
            locationButton.setImageResource(R.drawable.ic_mapposition_foreground);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //broadcast  conductor cerca
        if(broadcastReceiverMessage == null){
            broadcastReceiverMessage = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    notificacionReciber(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverMessage,new IntentFilter("conductor_cerca"));

        //broadcast  conductor llego
        if(broadcastReceiverMessageconductor == null){
            broadcastReceiverMessageconductor = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    conductor_llego(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverMessageconductor,new IntentFilter("conductor_llego"));

        //broadcast  inicio de carrera
        if(broadcastReceiverInicioCarrera == null){
            broadcastReceiverInicioCarrera = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Inicio_Carrera(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverInicioCarrera,new IntentFilter("Inicio_Carrera"));

        //Broadcast finalizo carrera
        if(broadcastReceiverFinalizoCarrera == null){
            broadcastReceiverFinalizoCarrera = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    finalizo_carrera(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverFinalizoCarrera,new IntentFilter("Finalizo_Carrera"));

        // Broadcast Cancelo el viaje el conductor
        if(broadcastReceiverCanceloCarrera == null){
            broadcastReceiverCanceloCarrera = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Cancelo_carrera(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverCanceloCarrera,new IntentFilter("cancelo_carrera"));

    }


    private boolean hilo;
    private void hilo(){
        hilo=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(hilo){
                    try {
                        new posicion_conductor().execute();
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    private void notificacionReciber(Intent intent){
        Toast.makeText(EsperandoConductor.this,"El conductor esta serca",
                Toast.LENGTH_SHORT).show();
    }

    private void conductor_llego(Intent intent){
        Toast.makeText(EsperandoConductor.this,"El conductor Llego",Toast.LENGTH_SHORT).show();

        ll_conductor_llego.setVisibility(View.VISIBLE);
        //perfil_condutor.setVisibility(View.VISIBLE);
    }

    private void Inicio_Carrera(Intent intent){
        Toast.makeText(EsperandoConductor.this,"Su carrera ha comenzado, Que tenga buen viaje.",
                Toast.LENGTH_SHORT).show();
        ll_conductor_llego.setVisibility(View.GONE);
        new buscar_carrera().execute();
        //perfil_condutor.setVisibility(View.VISIBLE);
    }

    private void finalizo_carrera(Intent intenta){
        Intent intent = new Intent( EsperandoConductor.this, finalizar_viajeCliente.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("id_carrera",intenta.getStringExtra("id_carrera"));
        startActivity(intent);
        finish();
    }

    private void Cancelo_carrera(Intent intenta){
        Intent intent = new Intent( EsperandoConductor.this, CanceloViaje_Cliente.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("id_carrera",intenta.getStringExtra("id_carrera"));
        startActivity(intent);
        finish();
    }


    private String obtenerDireccionesURL(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String key = "key="+getString(R.string.apikey);

        String parameters = str_origin+"&"+str_dest;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters+"&"+key;

        return url;
    }
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("ERROR AL OBTENER INFO D",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }
    private boolean first=false;
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.rgb(0,0,255));
            }
            if(lineOptions!=null) {

                googleMap.addPolyline(lineOptions);

                int size = points.size() - 1;
                float[] results = new float[1];
                float sum = 0;

                for(int i = 0; i < size; i++){
                    Location.distanceBetween(
                            points.get(i).latitude,
                            points.get(i).longitude,
                            points.get(i+1).latitude,
                            points.get(i+1).longitude,
                            results);
                    sum += results[0];
                }
                //sum = metros
            }
        }
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creamos una conexion http
            urlConnection = (HttpURLConnection) url.openConnection();

            // Conectamos
            urlConnection.connect();

            // Leemos desde URL
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class posicion_conductor extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            Hashtable<String,String> param = new Hashtable<>();
            try {
                int id=json_carrera.getInt("id");
                param.put("evento","get_pos_conductor_x_id_carrera");
                param.put("id",id+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);

            if (resp.equals("falso")) {
                Toast.makeText(EsperandoConductor.this,"No se Encontro Conductor Disculpe las Molestias",
                        Toast.LENGTH_SHORT).show();

                return;
            }else{
                try {
                    JSONObject obj=new JSONObject(resp);
                    LatLng ll1 = new LatLng(obj.getDouble("lat"),obj.getDouble("lng"));
                    LatLng ll2;
                    if(json_carrera.getInt("estado")==4){
                        ll2= new LatLng(json_carrera.getDouble("latfinal"),json_carrera.getDouble("lngfinal"));
                    }else{
                          ll2=new LatLng(json_carrera.getDouble("latinicial"),json_carrera.getDouble("lnginicial"));
                    }

                    String url = obtenerDireccionesURL(ll1,ll2);
                    DownloadTask downloadTask= new DownloadTask();
                    downloadTask.execute(url);
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(ll2).title("FIN").icon(BitmapDescriptorFactory.fromResource(R.drawable.asetmar)));
                    googleMap.addMarker(new MarkerOptions().position(ll1).title("AUTO").icon(BitmapDescriptorFactory.fromResource(R.drawable.taximark)));
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(ll1);
                    builder.include(ll2);
                    LatLngBounds bounds=builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
                    googleMap.moveCamera(cu);

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

    private class buscar_carrera extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(EsperandoConductor.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("obteniendo datos");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","get_carrera_id");
            try {
                param.put("id",json_carrera.getInt("id")+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();
            if (resp == null) {
                Toast.makeText(EsperandoConductor.this,"Eroor al optener Datos",
                        Toast.LENGTH_SHORT).show();
                return;
            }else{
                try {
                    JSONObject obj = new JSONObject(resp);
                    json_carrera=obj;
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
