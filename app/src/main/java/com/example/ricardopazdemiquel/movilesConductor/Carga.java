package com.example.ricardopazdemiquel.movilesConductor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ricardopazdemiquel.movilesConductor.R;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import utiles.Token;

public class Carga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga);
        Token.currentToken= FirebaseInstanceId.getInstance().getToken();
       // Log.d("TOKEN",Token.currentToken);
        final JSONObject usr_log = getUsr_log();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (usr_log != null) {
                    try {
                        if(usr_log.getInt("id_rol")==2){
                            Intent intent = new Intent(Carga.this, MainActivityConductor.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Intent intent = new Intent(Carga.this, LoginConductor.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }


            }
        }, 1000);

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
}
