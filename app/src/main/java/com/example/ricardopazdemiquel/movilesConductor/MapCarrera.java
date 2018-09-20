package com.example.ricardopazdemiquel.movilesConductor;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardopazdemiquel.movilesConductor.R;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
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
import java.util.BitSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;
import utiles.DirectionsJSONParser;

public class MapCarrera extends AppCompatActivity implements LocationListener, SensorEventListener {

    MapView mMapView;
    private GoogleMap googleMap;
    private int id_carrera = 0;
    private JSONObject carrera;
    private JSONObject usr_log;
    private FloatingActionButton btn_waze;
    private LocationManager locationManager;
    private BroadcastReceiver broadcastReceiverMessage;
    private BroadcastReceiver bradcastCanceloCarrera;
    private LinearLayout linear_marcar_llegada;
    private LinearLayout linear_Iniciar_Carrera;
    private LinearLayout cargandomapaline;
    private Button btn_marcar_llegada;
    private Button iniciar_Carrera;
    private double latwazefinal;
    private double lngwazefinal;
    //PERFIL DEL CLIENTE
    private ImageView img_foto;
    private TextView text_nombreCliente;
    private TextView text_data1;
    private TextView text_data2;
    private TextView text_Viajes;
    private Button btn_enviar_mensaje;
    private Button btn_llamar;
    ///////
    private Button btn_terminar_carrera;
    private Button btn_cancelar_carrera;
    private Button btn_costos_extras;
    private Location location;
    private CoordinatorLayout Container_verPerfil;
    private BottomSheetBehavior bottomSheetBehavior;
    private float currentDegree = 0f;
    // El sensor manager del dispositivo
    private SensorManager mSensorManager;
    // Los dos sensores que son necesarios porque TYPE_ORINETATION esta deprecated
    private Sensor accelerometer;
    private Sensor magnetometer;
    // Los angulos del movimiento de la flecha que señala al norte
    float degree;
    // Guarda el valor del azimut
    float azimut;
    // Guarda los valores que cambián con las variaciones del sensor TYPE_ACCELEROMETER
    float[] mGravity;
    // Guarda los valores que cambián con las variaciones del sensor TYPE_MAGNETIC_FIELD
    float[] mGeomagnetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_carrera);
        id_carrera = getIntent().getIntExtra("id_carrera", 0);
        View view = findViewById(R.id.button_sheet);
        Container_verPerfil = findViewById(R.id.Container_verPerfil);
        img_foto = findViewById(R.id.img_foto);
        text_nombreCliente = findViewById(R.id.text_nombreCliente);
        text_data1 = findViewById(R.id.text_data1);
        text_data2 = findViewById(R.id.text_data2);
        text_Viajes = findViewById(R.id.text_Viajes);
        cargandomapaline = findViewById(R.id.cargandomapaline);
        btn_llamar = findViewById(R.id.btn_llamar);
        btn_enviar_mensaje = findViewById(R.id.btn_enviar_mensaje);
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        btn_waze = findViewById(R.id.btn_ver_en_waze);
        //   btn_waze.setOnClickListener(this);
        btn_waze.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ver_en_waze();
                return false;
            }
        });
        btn_terminar_carrera = findViewById(R.id.btn_terminar_carrera);
        btn_cancelar_carrera = findViewById(R.id.btn_cancelar_carrera);
        btn_costos_extras = findViewById(R.id.btn_agregar_costo_extra);

        btn_marcar_llegada = findViewById(R.id.btn_marcar_llegada);
        iniciar_Carrera = findViewById(R.id.btn_iniciar_carrera);
        linear_Iniciar_Carrera = findViewById(R.id.linear_Iniciar_Carrera);
        linear_marcar_llegada = findViewById(R.id.linear_marcar_llegada);
        try {
            usr_log = getUsr_log();
            String resp = new buscar_carrera().execute().get();

            btn_cancelar_carrera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (carrera != null) {
                        try {
                            Intent inte = new Intent(MapCarrera.this, CancelarConductor.class);
                            inte.putExtra("id_carrera", carrera.getString("id"));
                            startActivity(inte);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            btn_costos_extras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (carrera != null) {
                        try {
                            Intent inte = new Intent(MapCarrera.this, CostosExtras.class);
                            inte.putExtra("id_carrera", carrera.getString("id"));
                            startActivity(inte);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        mMapView = findViewById(R.id.mapviewcarrera);
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
                //mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_map)));
                if (ActivityCompat.checkSelfPermission(MapCarrera.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapCarrera.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);


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
        if (ActivityCompat.checkSelfPermission(MapCarrera.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapCarrera.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 2, this);
        btn_terminar_carrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert();

            }
        });
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mGravity = null;
        mGeomagnetic = null;
        hilo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiverMessage == null) {
            broadcastReceiverMessage = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    notificacionReciber(intent);
                }
            };
        }
        registerReceiver(broadcastReceiverMessage, new IntentFilter("llego_conductor"));
        if (bradcastCanceloCarrera == null) {
            bradcastCanceloCarrera = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    cancelocarrera(intent);
                }
            };
        }
        registerReceiver(bradcastCanceloCarrera, new IntentFilter("Carrera_Cancelada"));

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

    }

    Intent inte;

    private void notificacionReciber(Intent intent) {
        linear_marcar_llegada.setVisibility(View.VISIBLE);
        btn_cancelar_carrera.setVisibility(View.GONE);
        btn_marcar_llegada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id_carrera > 0) {
                    //avisar llegada
                    new conductor_llego().execute();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void cancelocarrera(Intent intent) {
        Intent inten = new Intent(MapCarrera.this, CanceloViaje.class);
        inten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(inten);
    }

    private void ver_en_waze() {
        if (carrera != null) {
            try {

                String url = "https://waze.com/ul?ll=" + latwazefinal + "," + lngwazefinal + "&navigate=yes";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                // If Waze is not installed, open it in Google Play:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                startActivity(intent);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;


        if (auto == null) {

            auto = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("YO").icon(BitmapDescriptorFactory.fromResource(R.drawable.auto)).anchor(0.5f, 0.5f));
        } else {
            auto.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    private Marker marfin;
    private Marker auto;
    private float dist = 0;
    private boolean hilo;
    private JSONObject cliente;

    private void hilo() {
        hilo = true;

        new Thread() {
            public void run() {
                while (hilo) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (carrera != null && location != null && googleMap != null) {
                                    cargandomapaline.setVisibility(View.GONE);
                                    float acuracy = location.getAccuracy();
                                    LatLng latlng2 = null;
                                    LatLng latlng1 = null;
                                    try {
                                        if (acuracy < 30) {
                                            if (carrera.getInt("estado") == 2) {
                                                latlng2 = new LatLng(carrera.getDouble("latinicial"), carrera.getDouble("lnginicial"));
                                                latlng1 = new LatLng(location.getLatitude(), location.getLongitude());
                                                latwazefinal = latlng2.latitude;
                                                lngwazefinal = latlng2.longitude;
                                            } else if (carrera.getInt("estado") == 4) {
                                                btn_cancelar_carrera.setVisibility(View.INVISIBLE);
                                                latlng2 = new LatLng(carrera.getDouble("latfinal"), carrera.getDouble("lngfinal"));
                                                latlng1 = new LatLng(location.getLatitude(), location.getLongitude());
                                                latwazefinal = latlng2.latitude;
                                                lngwazefinal = latlng2.longitude;
                                                btn_terminar_carrera.setVisibility(View.VISIBLE);
                                                btn_costos_extras.setVisibility(View.VISIBLE);
                                            } else if (carrera.getInt("estado") == 3) {
                                                btn_cancelar_carrera.setVisibility(View.INVISIBLE);
                                                latlng2 = new LatLng(carrera.getDouble("latfinal"), carrera.getDouble("lngfinal"));
                                                latlng1 = new LatLng(location.getLatitude(), location.getLongitude());
                                                latwazefinal = latlng2.latitude;
                                                lngwazefinal = latlng2.longitude;
                                                linear_marcar_llegada.setVisibility(View.GONE);
                                                btn_waze.setVisibility(View.INVISIBLE);
                                                linear_Iniciar_Carrera.setVisibility(View.VISIBLE);
                                                if (cliente == null) {
                                                    new get_cliente().execute();
                                                    cliente = new JSONObject();
                                                }
                                                Container_verPerfil.setVisibility(View.VISIBLE);
                                                iniciar_Carrera.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (carrera != null)
                                                            try {
                                                                latwazefinal = carrera.getDouble("latfinal");
                                                                lngwazefinal = carrera.getDouble("lngfinal");
                                                                new iniciar_carrera().execute();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                    }
                                                });

                                            }
                                            if (latlng1 != null && latlng2 != null) {
                                                float[] results = new float[1];
                                                Location.distanceBetween(
                                                        latlng1.latitude,
                                                        latlng1.longitude,
                                                        latlng2.latitude,
                                                        latlng2.longitude,
                                                        results);
                                                if ((dist - results[0]) > 20 || (dist - results[0]) < -20 || dist == 0) {
                                                    googleMap.clear();
                                                    marfin = null;
                                                    auto = null;
                                                    dist = results[0];
                                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                    builder.include(latlng1);
                                                    builder.include(latlng2);
                                                    LatLngBounds bounds = builder.build();
                                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 300);
                                                    googleMap.moveCamera(cu);
                                                    String url = obtenerDireccionesURL(latlng1, latlng2);
                                                    DownloadTask downloadTask = new DownloadTask();
                                                    downloadTask.execute(url);
                                                }
                                                if (marfin == null) {
                                                    marfin = googleMap.addMarker(new MarkerOptions().position(latlng2).title("FIN").icon(BitmapDescriptorFactory.fromResource(R.drawable.asetmar)));
                                                } else {
                                                    marfin.setPosition(latlng2);
                                                }

                                                if (auto == null) {
                                                    auto = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("YO").icon(BitmapDescriptorFactory.fromResource(R.drawable.auto)).anchor(0.5f, 0.5f));
                                                } else {
                                                    auto.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

                                                }
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
                break;
        }

        if ((mGravity != null) && (mGeomagnetic != null)) {
            float RotationMatrix[] = new float[16];
            boolean success = SensorManager.getRotationMatrix(RotationMatrix, null, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(RotationMatrix, orientation);
                azimut = orientation[0] * (180 / (float) Math.PI);
            }
        }
        degree = azimut;


        currentDegree = -degree;
        if (auto != null) {

            auto.setRotation(currentDegree);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
            Hashtable<String, String> param = new Hashtable<>();
            param.put("evento", "get_carrera_id");
            param.put("id", id_carrera + "");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);

            progreso.dismiss();

            if (resp == null) {
                Toast.makeText(MapCarrera.this, "Error al optener datos.",
                        Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject obj = new JSONObject(resp);


                    carrera = obj;
                    SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
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
            Hashtable<String, String> param = new Hashtable<>();
            param.put("evento", "conductor_llego");
            param.put("id_carrera", id_carrera + "");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);

            progreso.dismiss();

            if (resp == null) {
                Toast.makeText(MapCarrera.this, "Eroor al optener Datos",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (resp.equals("exito")) {

                linear_marcar_llegada.setVisibility(View.GONE);
                btn_waze.setVisibility(View.INVISIBLE);
                linear_Iniciar_Carrera.setVisibility(View.VISIBLE);
                Container_verPerfil.setVisibility(View.VISIBLE);
                iniciar_Carrera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (carrera != null)
                            try {
                                latwazefinal = carrera.getDouble("latfinal");
                                lngwazefinal = carrera.getDouble("lngfinal");
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
            Hashtable<String, String> param = new Hashtable<>();
            param.put("evento", "inciar_carrera");
            param.put("id_carrera", id_carrera + "");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);

            progreso.dismiss();

            if (resp == null) {
                Toast.makeText(MapCarrera.this, "Eroor al optener Datos",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (resp.equals("exito")) {

                btn_waze.setVisibility(View.VISIBLE);
                iniciar_Carrera.setVisibility(View.GONE);
                linear_Iniciar_Carrera.setVisibility(View.GONE);
                Container_verPerfil.setVisibility(View.GONE);
                btn_terminar_carrera.setVisibility(View.VISIBLE);

                new buscar_carrera().execute();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progreso.setMessage(values[0]);
        }

    }

    private String number;
    private class get_cliente extends AsyncTask<Void, String, String> {
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
            try {
                publishProgress("por favor espere...");
                Hashtable<String, String> param = new Hashtable<>();
                param.put("evento", "get_cliente_x_id");
                param.put("id", carrera.getInt("id_usuario") + "");
                String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
                return respuesta;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);

            progreso.dismiss();

            if (resp == null) {
                Toast.makeText(MapCarrera.this, "Eroor al optener Datos",
                        Toast.LENGTH_SHORT).show();
            } else {
                try {
                    cliente = new JSONObject(resp);
                    text_nombreCliente.setText(cliente.getString("nombre") + " " + cliente.getString("apellido_pa") + " " + cliente.getString("apellido_ma"));
                    text_data1.setText(cliente.getString("fecha_nac"));
                    text_data2.setText(cliente.getString("sexo"));


                    btn_enviar_mensaje.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MapCarrera.this, Chat_Activity.class);
                            try {
                                intent.putExtra("id_receptor", cliente.getString("id"));
                                intent.putExtra("nombre_receptor", cliente.getString("nombre") + " " + cliente.getString("apellido_pa") + " " + cliente.getString("apellido_ma"));
                                intent.putExtra("id_emisor", usr_log.getString("id"));
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    btn_llamar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                String telefono = cliente.getString("telefono");
                                number=telefono;

                                int permissionCheck = ContextCompat.checkSelfPermission(
                                        MapCarrera.this, Manifest.permission.CALL_PHONE);
                                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                    Log.i("Mensaje", "No se tiene permiso para realizar llamadas telefónicas.");
                                    ActivityCompat.requestPermissions(MapCarrera.this, new String[]{Manifest.permission.CALL_PHONE}, 225);
                                } else {
                                     callPhone();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    if(cliente.has("foto_perfil")){
                        if (cliente.getString("foto_perfil").length() > 0) {
                            new AsyncTaskLoadImage(img_foto).execute(getString(R.string.url_foto) + cliente.getString("foto_perfil"));
                        }
                    }
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

    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapCarrera.this);
        builder.setMessage("Esta seguro que desea terminar el viaje?")
                .setTitle("Terminar Viaje")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CONFIRM
                        new terminar_Carrera(id_carrera).execute();

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 255: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone();
                } else {
                    System.out.println("El usuario ha rechazado el permiso");
                }
                return;
            }
        }
    }

    public void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
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

    // es con postgrebar
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

    private class terminar_Carrera extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private int id_carrera;

        public terminar_Carrera(int id_carrera) {
            this.id_carrera = id_carrera;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(MapCarrera.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "terminar_carrera");
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
                }else if(resp.equals("exito")){
                    try {
                        String a=new buscar_carrera().execute().get();
                        Intent intent = new Intent(MapCarrera.this, cobranza.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
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

}
