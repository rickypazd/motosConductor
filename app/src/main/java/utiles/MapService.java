package utiles;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ricardopazdemiquel.moviles.EsperandoConductor;
import com.example.ricardopazdemiquel.moviles.Login;
import com.example.ricardopazdemiquel.moviles.MainActivity;
import com.example.ricardopazdemiquel.moviles.MainActivityConductor;
import com.example.ricardopazdemiquel.moviles.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;

public class MapService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private int id_vehiculo;
    private boolean notifico;
    private boolean llego;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivityConductor.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this,Contexto.CHANNEL_ID)
                .setContentTitle("Siete")
                .setContentText("Siguiendo su Posicion.")
                .setSmallIcon(R.drawable.ic_logosiete_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);
        id_vehiculo=intent.getIntExtra("id_vehiculo",0);
        notifico=false;
        llego=false;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) { Intent i = new Intent("location_update");
                if(id_vehiculo>0){
                    new pushPosition(location.getLatitude(),location.getLongitude(),id_vehiculo).execute();
                    SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                    String carrera = preferencias.getString("carrera", "");
                    if (carrera.length()>0) {
                        try {
                            JSONObject obj = new JSONObject(carrera);
                            if(obj.getInt("estado")==3){
                                double latini=obj.getDouble("latinicial");
                                double lgnini=obj.getDouble("lnginicial");
                                double latfin=location.getLatitude();
                                double lngfin=location.getLongitude();
                                float result[] = new float[1];
                                Location.distanceBetween(latfin,lgnini,latfin,lngfin,result);
                                if(!notifico){
                                    if(result[0]<=300){
                                        new conductor_cerca(obj.getInt("id"),result[0]).execute();
                                        notifico=true;
                                    }
                                }
                                if(!llego){
                                    if(result[0]<=50){
                                        Intent intent = new Intent();
                                        intent.putExtra("message","");
                                        intent.setAction("llego_conductor");
                                        sendBroadcast(intent);
                                        llego=true;
                                    }
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener);

    }
    private class pushPosition extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private double lat;
        private double lon;
        private int id;

        public pushPosition(double lat, double lon, int id){
            this.lat=lat;
            this.lon=lon;
            this.id=id;
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
