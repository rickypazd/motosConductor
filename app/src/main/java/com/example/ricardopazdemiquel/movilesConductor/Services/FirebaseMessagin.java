package com.example.ricardopazdemiquel.movilesConductor.Services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ricardopazdemiquel.movilesConductor.Cofirmar_Carrera;
import com.example.ricardopazdemiquel.movilesConductor.MainActivityConductor;
import com.example.ricardopazdemiquel.movilesConductor.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utiles.Contexto;
import utiles.Single;

import static android.content.ContentValues.TAG;

public class FirebaseMessagin extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().size()==0){
            return;
        }
        switch (remoteMessage.getData().get("evento")){
            case "confirmar_carrera":
                confirmar_carrera(remoteMessage);
                break;
            case "Carrera_Cancelada":
                Carrera_Cancelada(remoteMessage);
                break;
            case "mensaje_recibido":
                mensaje_recibido(remoteMessage);
                break;
            case "mensaje":
                mensaje(remoteMessage);
                break;

        }
        return;
    }

    private void mensaje_recibido(RemoteMessage remoteMessage) {
        try {
            JSONObject obj = new JSONObject(remoteMessage.getData().get("json"));
            setMensaje(obj);
            Intent notificationIntent = new Intent(this, MainActivityConductor.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
            Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                    .setContentTitle("Siete: Nuevo mensaje")
                    .setContentText(obj.getString("mensaje"))
                    .setSmallIcon(R.drawable.ic_logosiete_background)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(3,notification);
            Intent intent = new Intent();
            intent.putExtra("obj",obj.toString());
            intent.setAction("nuevo_mensaje");
            sendBroadcast(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void setMensaje(JSONObject mensaje){
        JSONArray mensajes= getChat();
        if(mensajes==null){
            mensajes=new JSONArray();
        }
        mensajes.put(mensaje);
        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("chat_carrera", mensajes.toString());
        editor.commit();

    }
    public JSONArray getChat() {
        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
        String usr = preferencias.getString("chat_carrera", "");
        if (usr.length() <= 0) {
            return null;
        } else {
            try {
                JSONArray chat = new JSONArray(usr);
                return chat;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void Carrera_Cancelada(RemoteMessage remoteMessage) {
        Intent intent = new Intent();
        intent.putExtra("json",remoteMessage.getData().get("json"));
        intent.setAction("Carrera_Cancelada");
        sendBroadcast(intent);
    }


    private void confirmar_carrera(RemoteMessage remoteMessage) {
        Intent intent = new Intent();
        JSONObject json = null;
        try {
            json = new JSONObject(remoteMessage.getData().get("json"));
            intent.putExtra("json" , json.toString());

            JSONObject jsonUsuario = new JSONObject(remoteMessage.getData().get("jsonUsuario"));

            intent.putExtra("jsonUsuario" , jsonUsuario.toString());
            intent.setAction("confirmar_carrera");
            sendBroadcast(intent);


                notification5(10,R.drawable.ic_logosiete_foreground,"Siete","Viaje Entrante",jsonUsuario,json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void mensaje(RemoteMessage remoteMessage){
        Intent intent = new Intent();
        intent.putExtra("message",remoteMessage.getData().get("mensaje"));
        intent.setAction("Message");
        sendBroadcast(intent);
    }



    private void Carrera_terminada(RemoteMessage remoteMessage) {
        Intent intent = new Intent();
        JSONObject json = null;
        try {
            json = new JSONObject(remoteMessage.getData().get("json"));
            intent.putExtra("json" , json.toString());
            intent.setAction("confirmar_carrera");
            sendBroadcast(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void mensajeCT(RemoteMessage remoteMessage){
        Intent intent = new Intent();
        intent.putExtra("message",remoteMessage.getData().get("mensaje"));
        intent.setAction("Message");
        sendBroadcast(intent);
    }

    public void notification5(final int id, int iconId, String titulo, String contenido,JSONObject jsonUsuario, JSONObject json) {
        Intent intenta = new Intent(this, Cofirmar_Carrera.class);
        intenta.putExtra("jsonUsuario" , jsonUsuario.toString());
        intenta.putExtra("json" , json.toString());
        intenta.putExtra("tiempo",10);
        intenta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intenta, 0);

        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(iconId)
                        .setContentTitle(titulo)
                        .setContentIntent(pendingIntent)
                        .setContentText(contenido).setNumber(10);

        //builder.addAction(android.R.drawable.ic_menu_share, "ver", pendingIntent);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(defaultSound);
        long[] pattern = new long[]{1000,500,1000};
        builder.setVibrate(pattern);
        builder.setOngoing(true);
        builder.setAutoCancel(true);
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int i;
                        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        // Ciclo para la simulación de progreso
                        for (i = 0; i <= 100; i += 5) {
                            // Setear 100% como medida máxima
                            builder.setProgress(100, i, false);
                            // Emitir la notificación
                            //intenta.putExtra("tiempo",i);
                           // PendingIntent pendingIntent=PendingIntent.getActivity(FirebaseMessagin.this,0,intenta,0);
                           // builder.setContentIntent(pendingIntent);
                            Single.settiempo((i/5)*500);
                            notificationManager.notify(id, builder.build());
                            // Retardar la ejecución del hilo
                            try {
                                // Retardo de 1s
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                Log.d(TAG, "Falló sleep(1000) ");
                            }
                        }

                    /*
                    ACTUALIZACIÓN DE LA NOTIFICACION
                     */
                        Single.settiempo(0);
                        notificationManager.cancel(10);
                    }
                }

        ).start();

    }
}
