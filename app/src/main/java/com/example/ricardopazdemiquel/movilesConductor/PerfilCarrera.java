package com.example.ricardopazdemiquel.movilesConductor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;


public class PerfilCarrera extends AppCompatActivity {

    MapView mMapView;
    private GoogleMap googleMap;
    private int id_carrera;
    private JSONObject carrera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_carrera);
        id_carrera=getIntent().getIntExtra("id_carrera",0);
        if(id_carrera>0){
            try {
                String resp = new buscar_carrera().execute().get();
                if (resp == null) {
                    Toast.makeText(PerfilCarrera.this,"Error al obtener Datos.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        carrera = new JSONObject(resp);
                        mMapView=findViewById(R.id.mapviewdetalle);
                        mMapView.onCreate(savedInstanceState);
                        mMapView.onResume();
                        MapsInitializer.initialize(this.getApplicationContext());
                        mMapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap mMap) {
                                googleMap = mMap;
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(-17.78629, -63.18117))      // Sets the center of the map to Mountain View
                                        .zoom(17)                   // Sets the zoom
                                        .build();                   // Creates a CameraPosition from the builder
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                if(carrera!= null){
                                    try {
                                        ArrayList<LatLng> points = new ArrayList<>();
                                        JSONArray arr = carrera.getJSONArray("recorrido");
                                        JSONObject obj;
                                        JSONObject obj2;
                                        LatLng latlng1;
                                        Location location1;
                                        Location location2;
                                        double dist=0;
                                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_action_name);
                                        Bitmap b=bitmapdraw.getBitmap();
                                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 10, 10, false);
                                        for (int i = 0; i <arr.length()-1 ; i++) {
                                            obj=arr.getJSONObject(i);
                                            obj2=arr.getJSONObject(i+1);
                                            location1= new Location("a");
                                            location2=new Location("b");
                                            location1.setLatitude(obj.getDouble("lat"));
                                            location1.setLongitude(obj.getDouble("lng"));
                                            location2.setLatitude(obj2.getDouble("lat"));
                                            location2.setLongitude(obj2.getDouble("lng"));

                                            dist+=location1.distanceTo(location2);
                                            latlng1=new LatLng(obj.getDouble("lat"),obj.getDouble("lng"));
                                            googleMap.addMarker(new MarkerOptions()
                                                    .position(latlng1)
                                                    .title(obj.getString("fecha"))
                                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                            );
                                            points.add(latlng1);
                                            if(i==arr.length()-1){
                                                LatLng latLng2= new LatLng(obj2.getDouble("lat"),obj2.getDouble("lng"));
                                                points.add(latLng2);
                                            }

                                        }
                                        if(points.size()>0){
                                            PolylineOptions lineOptions = new PolylineOptions();
                                            lineOptions.addAll(points);
                                            lineOptions.width(8);
                                            lineOptions.color(Color.rgb(0,0,255));
                                            //  googleMap.addPolyline(lineOptions);
                                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            builder.include(points.get(0));
                                            builder.include(points.get(points.size()-1));
                                            LatLngBounds bounds=builder.build();
                                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
                                            googleMap.moveCamera(cu);

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }
    private class buscar_carrera extends AsyncTask<Void, String, String> {
        private ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(PerfilCarrera.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("obteniendo datos");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","get_carrera_id_recorrido");
            param.put("id",id_carrera+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progreso.setMessage(values[0]);
        }

    }
}
