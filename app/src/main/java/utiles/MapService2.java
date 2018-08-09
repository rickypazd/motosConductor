package utiles;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.example.ricardopazdemiquel.movilesConductor.MainActivityConductor;
import com.example.ricardopazdemiquel.movilesConductor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;

import static android.content.ContentValues.TAG;

public class MapService2 extends Service implements LocationListener, GpsStatus.Listener,SensorEventListener {
    public static final String LOG_TAG = MapService2.class.getSimpleName();

    private final LocationServiceBinder binder = new LocationServiceBinder();
    boolean isLocationManagerUpdatingLocation;
    private int id_vehiculo;
    private boolean notifico;
    private boolean llego;
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
    ArrayList<Location> locationList;

    ArrayList<Location> oldLocationList;
    ArrayList<Location> noAccuracyLocationList;
    ArrayList<Location> inaccurateLocationList;
    ArrayList<Location> kalmanNGLocationList;


    boolean isLogging;

    float currentSpeed = 0.0f; // meters/second

    KalmanLatLong kalmanFilter;
    long runStartTimeInMillis;

    ArrayList<Integer> batteryLevelArray;
    ArrayList<Float> batteryLevelScaledArray;
    int batteryScale;
    int gpsCount;


    public MapService2() {

    }

    @Override
    public void onCreate() {
        isLocationManagerUpdatingLocation = false;
        locationList = new ArrayList<>();
        noAccuracyLocationList = new ArrayList<>();
        oldLocationList = new ArrayList<>();
        inaccurateLocationList = new ArrayList<>();
        kalmanNGLocationList = new ArrayList<>();
        kalmanFilter = new KalmanLatLong(3);

        isLogging = true;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mGravity = null;
        mGeomagnetic = null;
        batteryLevelArray = new ArrayList<>();
        batteryLevelScaledArray = new ArrayList<>();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        startUpdatingLocation();
    }



    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivityConductor.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this,Contexto.CHANNEL_ID)
                .setContentTitle("Siete")
                .setContentText("Siguiendo su Posicion.")
                .setSmallIcon(R.drawable.ic_logosiete_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);
        id_vehiculo=i.getIntExtra("id_vehiculo",0);
        notifico=false;
        llego=false;
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onRebind(Intent intent) {
        Log.d(LOG_TAG, "onRebind ");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "onUnbind ");

        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy ");


    }


    //This is where we detect the app is being killed, thus stop service.
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(LOG_TAG, "onTaskRemoved ");
        this.stopUpdatingLocation();

        stopSelf();
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
            boolean success = SensorManager.getRotationMatrix(RotationMatrix,                                                             null, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(RotationMatrix, orientation);
                azimut = orientation[0] * (180 / (float) Math.PI);
            }
        }
        degree = azimut;


        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    /**
     * Binder class
     *
     * @author Takamitsu Mizutori
     *
     */
    public class LocationServiceBinder extends Binder {
        public MapService2 getService() {
            return MapService2.this;
        }
    }



    /* LocationListener implemenation */
    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            notifyLocationProviderStatusUpdated(false);
        }

    }

    @Override
    public void onProviderEnabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            notifyLocationProviderStatusUpdated(true);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            if (status == LocationProvider.OUT_OF_SERVICE) {
                notifyLocationProviderStatusUpdated(false);
            } else {
                notifyLocationProviderStatusUpdated(true);
            }
        }
    }

    /* GpsStatus.Listener implementation */
    public void onGpsStatusChanged(int event) {


    }

    private void notifyLocationProviderStatusUpdated(boolean isLocationProviderAvailable) {
        //Broadcast location provider status change here
    }

    public void startLogging(){
        isLogging = true;

    }

    public void stopLogging(){
        if (locationList.size() > 1 && batteryLevelArray.size() > 1){
            long currentTimeInMillis = (long)(SystemClock.elapsedRealtimeNanos() / 1000000);
            long elapsedTimeInSeconds = (currentTimeInMillis - runStartTimeInMillis) / 1000;
            float totalDistanceInMeters = 0;
            for(int i = 0; i < locationList.size() - 1; i++){
                totalDistanceInMeters +=  locationList.get(i).distanceTo(locationList.get(i + 1));
            }
            int batteryLevelStart = batteryLevelArray.get(0).intValue();
            int batteryLevelEnd = batteryLevelArray.get(batteryLevelArray.size() - 1).intValue();

            float batteryLevelScaledStart = batteryLevelScaledArray.get(0).floatValue();
            float batteryLevelScaledEnd = batteryLevelScaledArray.get(batteryLevelScaledArray.size() - 1).floatValue();

            saveLog(elapsedTimeInSeconds, totalDistanceInMeters, gpsCount, batteryLevelStart, batteryLevelEnd, batteryLevelScaledStart, batteryLevelScaledEnd);

        }
        isLogging = false;
    }



    public void startUpdatingLocation() {
        if(this.isLocationManagerUpdatingLocation == false){
            isLocationManagerUpdatingLocation = true;
            runStartTimeInMillis = (long)(SystemClock.elapsedRealtimeNanos() / 1000000);


            locationList.clear();

            oldLocationList.clear();
            noAccuracyLocationList.clear();
            inaccurateLocationList.clear();
            kalmanNGLocationList.clear();

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //Exception thrown when GPS or Network provider were not available on the user's device.
            try {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE); //setAccuracyは内部では、https://stackoverflow.com/a/17874592/1709287の用にHorizontalAccuracyの設定に変換されている。
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                criteria.setAltitudeRequired(false);
                criteria.setSpeedRequired(false);
                criteria.setCostAllowed(true);
                criteria.setBearingRequired(false);

                //API level 9 and up
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                //criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
                //criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);

                Integer gpsFreqInMillis = 100;
                Integer gpsFreqInDistance = 15;  // in meters

                locationManager.addGpsStatusListener(this);

                locationManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, this, null);

                /* Battery Consumption Measurement */
                gpsCount = 0;
                batteryLevelArray.clear();
                batteryLevelScaledArray.clear();

            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            } catch (SecurityException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            } catch (RuntimeException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            }
        }
    }


    public void stopUpdatingLocation(){
        if(this.isLocationManagerUpdatingLocation == true){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.removeUpdates(this);
            isLocationManagerUpdatingLocation = false;
        }
    }

    @Override
    public void onLocationChanged(final Location newLocation) {
        Log.d(TAG, "(" + newLocation.getLatitude() + "," + newLocation.getLongitude() + ")");

        gpsCount++;

        if(isLogging){
            //locationList.add(newLocation);
            filterAndAddLocation(newLocation);
        }
    }

    @SuppressLint("NewApi")
    private long getLocationAge(Location newLocation){
        long locationAge;
        if(android.os.Build.VERSION.SDK_INT >= 17) {
            long currentTimeInMilli = (long)(SystemClock.elapsedRealtimeNanos() / 1000000);
            long locationTimeInMilli = (long)(newLocation.getElapsedRealtimeNanos() / 1000000);
            locationAge = currentTimeInMilli - locationTimeInMilli;
        }else{
            locationAge = System.currentTimeMillis() - newLocation.getTime();
        }
        return locationAge;
    }


    private boolean filterAndAddLocation(Location location){

      long age = getLocationAge(location);

        if(age > 5 * 1000){ //more than 5 seconds
            Log.d(TAG, "Location is old");
            oldLocationList.add(location);
            return false;
        }

        if(location.getAccuracy() <= 0){
            Log.d(TAG, "Latitidue and longitude values are invalid.");
            return false;
        }

        //setAccuracy(newLocation.getAccuracy());
        float horizontalAccuracy = location.getAccuracy();
        if(horizontalAccuracy > 25){ //10meter filter
            Log.d(TAG, "Accuracy is too low.");
            return false;
        }

        if(id_vehiculo>0) {
            float acuracy =location.getAccuracy();
            if(acuracy<30){
                new pushPosition(location.getLatitude(), location.getLongitude(), id_vehiculo,currentDegree).execute();
                SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
                String carrera = preferencias.getString("carrera", "");
                if (carrera.length() > 0) {
                    try {

                        JSONObject obj = new JSONObject(carrera);
                        if (obj.getInt("estado") == 2) {
                            double latini = obj.getDouble("latinicial");
                            double lgnini = obj.getDouble("lnginicial");
                            double latfin = location.getLatitude();
                            double lngfin = location.getLongitude();
                            float result[] = new float[1];
                            Location.distanceBetween(latfin, lgnini, latfin, lngfin, result);
                            if (!notifico) {
                                if (result[0] <= 200) {
                                    new conductor_cerca(obj.getInt("id"), result[0]).execute();
                                    notifico = true;
                                }
                            }
                            if (!llego) {
                                if (result[0] <= 40) {
                                    Intent intent = new Intent();
                                    intent.putExtra("message", "");
                                    intent.setAction("llego_conductor");
                                    sendBroadcast(intent);
                                    llego = true;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    notifico = false;
                    llego = false;
                }
            }
        }

        return true;
    }



    /* Battery Consumption */
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryLevelScaled = batteryLevel / (float)scale;
            batteryLevelArray.add(Integer.valueOf(batteryLevel));
            batteryLevelScaledArray.add(Float.valueOf(batteryLevelScaled));
            batteryScale = scale;
        }
    };

    /* Data Logging */
    public synchronized void saveLog(long timeInSeconds, double distanceInMeters, int gpsCount, int batteryLevelStart, int batteryLevelEnd, float batteryLevelScaledStart, float batteryLevelScaledEnd) {
        SimpleDateFormat fileNameDateTimeFormat = new SimpleDateFormat("yyyy_MMdd_HHmm");
        String filePath = this.getExternalFilesDir(null).getAbsolutePath() + "/"
                + fileNameDateTimeFormat.format(new Date()) + "_battery" + ".csv";

        Log.d(TAG, "saving to " + filePath);

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, false);
            fileWriter.append("Time,Distance,GPSCount,BatteryLevelStart,BatteryLevelEnd,BatteryLevelStart(/" + batteryScale + ")," + "BatteryLevelEnd(/" + batteryScale + ")" + "\n");
            String record = "" + timeInSeconds + ',' + distanceInMeters + ',' + gpsCount + ',' + batteryLevelStart + ',' + batteryLevelEnd + ',' + batteryLevelScaledStart + ',' + batteryLevelScaledEnd + '\n';
            fileWriter.append(record);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }


    private class pushPosition extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private double lat;
        private double lon;
        private int id;
        private float bearing;

        public pushPosition(double lat, double lon, int id, float bearing){
            this.lat=lat;
            this.lon=lon;
            this.id=id;
            this.bearing=bearing;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "set_pos_vehiculo");
            parametros.put("lat",lat+"");
            parametros.put("lon",lon+"");
            parametros.put("id_vehiculo",id+"");
            parametros.put("bearing", bearing+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

    }

    private class conductor_cerca extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private int id;
        private float distancia;

        public conductor_cerca(int id,float distancia){
            this.id=id;
            this.distancia=distancia;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "conductor_cerca");

            parametros.put("id_carrera",id+"");
            parametros.put("distancia",distancia+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

    }
}