package com.example.ricardopazdemiquel.movilesConductor.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.ricardopazdemiquel.movilesConductor.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

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

        }
        return;
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
