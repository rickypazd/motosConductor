package com.example.ricardopazdemiquel.moviles;

import android.Manifest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.DirectionsJSONParser;
import utiles.Token;

public class MapCarrera extends AppCompatActivity {

    MapView mMapView;
    private GoogleMap googleMap;
    private int id_carrera = 0;
    private JSONObject carrera;
    private FloatingActionButton btn_waze;
    private LocationManager locationManager;
    private BroadcastReceiver broadcastReceiverMessage;
    private LinearLayout linear_marcar_llegada;
    private LinearLayout linear_Iniciar_Carrera;
    private Button btn_marcar_llegada;
    private Button iniciar_Carrera;
    private double latwazefinal;
    private double lngwazefinal;
    private Button btn_terminar_carrera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_carrera);
        id_carrera = getIntent().getIntExtra("id_carrera", 0);
        btn_waze = findViewById(R.id.btn_ver_en_waze);
     //   btn_waze.setOnClickListener(this);
            btn_waze.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ver_en_waze();
                    return false;
                }
            });
        btn_terminar_carrera=findViewById(R.id.btn_terminar_carrera);

        btn_marcar_llegada=findViewById(R.id.btn_marcar_llegada);
        iniciar_Carrera=findViewById(R.id.btn_iniciar_carrera);
        linear_Iniciar_Carrera=findViewById(R.id.linear_Iniciar_Carrera);
        linear_marcar_llegada= findViewById(R.id.linear_marcar_llegada);
        new buscar_carrera().execute();
        mMapView=findViewById(R.id.mapviewcarrera);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        MapsInitializer.initialize(this.getApplicationContext());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                //mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_map)));
                if (ActivityCompat.checkSelfPermission(MapCarrera.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapCarrera.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mMap.setMyLocationEnabled(true);

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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if (carrera!=null){
                    LatLng latlng2= null;
                    LatLng latlng1= null;
                    try {
                        if(carrera.getInt("estado")==2){
                            latlng2 = new LatLng(carrera.getDouble("latinicial"),carrera.getDouble("lnginicial"));
                            latlng1 = new LatLng(location.getLatitude(),location.getLongitude());
                            latwazefinal=latlng2.latitude;
                            lngwazefinal=latlng2.longitude;
                            String url = obtenerDireccionesURL(latlng1,latlng2);
                            DownloadTask downloadTask= new DownloadTask();
                            downloadTask.execute(url);
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(latlng2).title("FIN").icon(BitmapDescriptorFactory.fromResource(R.drawable.asetmar)));
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(latlng1);
                            builder.include(latlng2);
                            LatLngBounds bounds=builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
                            googleMap.moveCamera(cu);
                        }else if(carrera.getInt("estado")==4){
                            latlng2 = new LatLng(carrera.getDouble("latfinal"),carrera.getDouble("lngfinal"));
                            latlng1 = new LatLng(location.getLatitude(),location.getLongitude());
                            latwazefinal=latlng2.latitude;
                            lngwazefinal=latlng2.longitude;
                            String url = obtenerDireccionesURL(latlng1,latlng2);
                            DownloadTask downloadTask= new DownloadTask();
                            downloadTask.execute(url);
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(latlng2).title("FIN").icon(BitmapDescriptorFactory.fromResource(R.drawable.asetmar)));
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(latlng1);
                            builder.include(latlng2);
                            LatLngBounds bounds=builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
                            googleMap.moveCamera(cu);
                            btn_terminar_carrera.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 5, locationListener);


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(broadcastReceiverMessage == null){
            broadcastReceiverMessage = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    notificacionReciber(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverMessage,new IntentFilter("llego_conductor"));
    }
    Intent inte;
    private void notificacionReciber(Intent intent){
        linear_marcar_llegada.setVisibility(View.VISIBLE);
        btn_marcar_llegada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id_carrera>0){
                    //avisar llegada
                    new conductor_llego().execute();
                }
            }
        });
    }

    private void ver_en_waze(){
        if(carrera!=null){
            try
            {

                String url = "https://waze.com/ul?ll="+latwazefinal+","+lngwazefinal+"&navigate=yes";
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                startActivity( intent );
            }
            catch ( ActivityNotFoundException ex  )
            {
                // If Waze is not installed, open it in Google Play:
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                startActivity(intent);
            }
        }
    }



    private class buscar_carrera extends AsyncTask<Void, String, String> {
        private ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progreso = new ProgressDialog(MapCarrera.this);
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
            param.put("id",id_carrera+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);

            progreso.dismiss();

            if (resp == null) {
                Toast.makeText(MapCarrera.this,"Eroor al optener Datos",
                        Toast.LENGTH_SHORT).show();
                return;
            }else{
                try {
                    JSONObject obj = new JSONObject(resp);
                    carrera=obj;
                    SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putString("carrera", obj.toString());

                    editor.commit();
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
    private class conductor_llego extends AsyncTask<Void, String, String> {
        private ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progreso = new ProgressDialog(MapCarrera.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Confirmando Llegada.");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","conductor_llego");
            param.put("id_carrera",id_carrera+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);

            progreso.dismiss();

            if (resp == null) {
                Toast.makeText(MapCarrera.this,"Eroor al optener Datos",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if(resp.equals("exito")){

                linear_marcar_llegada.setVisibility(View.GONE);
                btn_waze.setVisibility(View.INVISIBLE);
                linear_Iniciar_Carrera.setVisibility(View.VISIBLE);
                iniciar_Carrera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(carrera!=null)
                        try {
                            latwazefinal=carrera.getDouble("latfinal");
                            lngwazefinal=carrera.getDouble("lngfinal");
                            new iniciar_carrera().execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                new buscar_carrera().execute();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progreso.setMessage(values[0]);
        }

    }
    private class iniciar_carrera extends AsyncTask<Void, String, String> {
        private ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progreso = new ProgressDialog(MapCarrera.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Iniciando Carrera.");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","inciar_carrera");
            param.put("id_carrera",id_carrera+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);

            progreso.dismiss();

            if (resp == null) {
                Toast.makeText(MapCarrera.this,"Eroor al optener Datos",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if(resp.equals("exito")){

                btn_waze.setVisibility(View.VISIBLE);
                linear_Iniciar_Carrera.setVisibility(View.GONE);
                btn_terminar_carrera.setVisibility(View.VISIBLE);
                btn_terminar_carrera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert();

                    }
                });
                new buscar_carrera().execute();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progreso.setMessage(values[0]);
        }

    }
    private void alert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MapCarrera.this);
        builder.setMessage("Esta seguro que quiere terminar la carrera?")
                .setTitle("Terminar Carrera")
                .setPositiveButton("Si", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        // CONFIRM
                        Intent intent = new Intent(MapCarrera.this, cobranza.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    private String obtenerDireccionesURL(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String sensor = "sensor=false";
        String key = "key="+getString(R.string.apikey);

        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters+"?"+key;

        return url;
    }
    public JSONObject getUsr_log() {
        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
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

            MapCarrera.ParserTask parserTask = new MapCarrera.ParserTask();

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
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(points.get(0));
                builder.include(points.get(points.size()-1));
                LatLngBounds bounds=builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
                googleMap.moveCamera(cu);

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
}
