package com.example.ricardopazdemiquel.moviles;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Elejir_tipo_siete extends AppCompatActivity implements View.OnClickListener {

    private Button siete;
    private Button siete_maravilla;
    private Button siete_super;
    private Button siete_togo;

    double longitudeGPS, latitudeGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elejir_tipo_siete);

        longitudeGPS = getIntent().getDoubleExtra("lng",0);
        latitudeGPS = getIntent().getDoubleExtra("lat",0);

        siete = findViewById(R.id.btn_siete);
        siete_maravilla = findViewById(R.id.btn_sieteMaravilla);
        siete_super = findViewById(R.id.btn_superSiete);
        siete_togo = findViewById(R.id.btn_togo);

        siete.setOnClickListener(this);
        siete_maravilla.setOnClickListener(this);
        siete_super.setOnClickListener(this);
        siete_togo.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Elejir_tipo_siete.this, PedirSieteMap.class);
        switch (view.getId()) {
            case R.id.btn_siete:
                if (longitudeGPS != 0) {
                    intent.putExtra("lng", longitudeGPS);
                    intent.putExtra("lat", latitudeGPS);
                    intent.putExtra("tipo", 1);
                }
                break;
            case R.id.btn_superSiete:
                if (longitudeGPS != 0) {
                    intent.putExtra("lng", longitudeGPS);
                    intent.putExtra("lat", latitudeGPS);
                    intent.putExtra("tipo", 2);
                }
                break;
            case R.id.btn_sieteMaravilla:
                if (longitudeGPS != 0) {
                    intent.putExtra("lng", longitudeGPS);
                    intent.putExtra("lat", latitudeGPS);
                    intent.putExtra("tipo", 3);
                }
                break;
            case R.id.btn_togo:
                if (longitudeGPS != 0) {
                    intent.putExtra("lng", longitudeGPS);
                    intent.putExtra("lat", latitudeGPS);
                    intent.putExtra("tipo", 4);
                }
                break;
        }
        startActivity(intent);
    }


}