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
import com.example.ricardopazdemiquel.moviles.finalizar_viajeCliente;
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
            case "Finalizo_Carrera":
                Finalizo_Carrera(remoteMessage);
                break;
            case "Carrera_Cancelada":
                Cancelo_carrera(remoteMessage);
                break;
        }
        return;

    }

    private void Finalizo_Carrera(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, finalizar_viajeCliente.class);
        notificationIntent.putExtra("id_carrera",remoteMessage.getData().get("json"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                .setContentTitle("Siete")
                .setContentText("Su Carrera finalizo.")
                .setSmallIcon(R.drawable.ic_logosiete_background)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
        Intent intent = new Intent();
        intent.putExtra("message",remoteMessage.getData().get("mensaje"));
        intent.putExtra("id_carrera",remoteMessage.getData().get("json"));
        intent.setAction("Finalizo_Carrera");
        sendBroadcast(intent);
    }


    private void Inicio_Carrera(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, EsperandoConductor.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                .setContentTitle("Siete")
                .setContentText("Su Carrera esta en curso." +
                        "Que tenga un buen viaje.")
                .setSmallIcon(R.drawable.ic_logosiete_background)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
        Intent intent = new Intent();
        intent.putExtra("obj_carrera",remoteMessage.getData().get("json"));
        intent.setAction("Inicio_Carrera");
        sendBroadcast(intent);
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
        intent.putExtra("obj_carrera",remoteMessage.getData().get("jso"));
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
        intent.putExtra("obj_carrera",remoteMessage.getData().get("json"));
        intent.setAction("conductor_cerca");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void Cancelo_carrera(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, EsperandoConductor.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                .setContentTitle("Siete")
                .setContentText("El conductor canceló el viaje. disculpe las molestias")
                .setSmallIcon(R.drawable.ic_logosiete_background)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
        Intent intent = new Intent();
        intent.putExtra("obj_carrera",remoteMessage.getData().get("json"));
        intent.setAction("cancelo_carrera");
        sendBroadcast(intent);
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

}
