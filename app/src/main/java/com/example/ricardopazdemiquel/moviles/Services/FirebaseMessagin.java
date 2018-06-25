package com.example.ricardopazdemiquel.moviles.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.ricardopazdemiquel.moviles.EsperandoConductor;
import com.example.ricardopazdemiquel.moviles.MainActivityConductor;
import com.example.ricardopazdemiquel.moviles.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import utiles.Contexto;

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
            case "mensaje":
                mensaje(remoteMessage);
                break;
            case "conductor_cerca":
                conductor_cerca(remoteMessage);
                break;
            case "conductor_llego":
                conductor_llego(remoteMessage);
                break;
            case "Inicio_Carrera":
                Inicio_Carrera(remoteMessage);
                break;
        }
        return;

    }

    private void Inicio_Carrera(RemoteMessage remoteMessage) {
    }

    private void conductor_llego(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, EsperandoConductor.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                .setContentTitle("Siete")
                .setContentText("Su Conductor Llego.")
                .setSmallIcon(R.drawable.ic_logosiete_background)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
        Intent intent = new Intent();
        intent.putExtra("message",remoteMessage.getData().get("mensaje"));
        intent.setAction("conductor_llego");
        sendBroadcast(intent);
    }

    private void conductor_cerca(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, EsperandoConductor.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                .setContentTitle("Siete")
                .setContentText("Su Conductor esta serca.")
                .setSmallIcon(R.drawable.ic_logosiete_background)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
        Intent intent = new Intent();
        intent.putExtra("message",remoteMessage.getData().get("mensaje"));
        intent.setAction("conductor_cerca");
        sendBroadcast(intent);
    }

    private void confirmar_carrera(RemoteMessage remoteMessage) {
        Intent intent = new Intent();
        JSONObject json = null;
        try {
            json = new JSONObject(remoteMessage.getData().get("json"));
            intent.putExtra("id", (Bundle) json.get("id"));
            intent.putExtra("latinicial", (Bundle) json.get("latinicial"));
            intent.putExtra("lnginicial", (Bundle) json.get("lnginicial"));
            intent.putExtra("latfinal", (Bundle) json.get("latfinal"));

            JSONObject jsonUsuario = new JSONObject(remoteMessage.getData().get("jsonUsuario"));
            intent.putExtra("nombre", (Bundle) jsonUsuario.get("nombre"));
            intent.putExtra("apellido_pa", (Bundle) jsonUsuario.get("apellido_pa"));
            intent.putExtra("apellido_ma", (Bundle) jsonUsuario.get("apellido_ma"));
            intent.putExtra("sexo", (Bundle) jsonUsuario.get("sexo"));
            intent.setAction("confirmar_carrera");
            sendBroadcast(intent);

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
}
