package com.example.ricardopazdemiquel.moviles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

public class finalizar_viajeCliente extends AppCompatActivity implements View.OnClickListener{


    private Button nombre;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_viajecliente);

        nombre = findViewById(R.id.btn_nombre);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        ratingBar.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        float getrating = ratingBar.getRating();
        Toast.makeText (this, "Calificación:" + getrating , Toast .LENGTH_LONG) .show ();
    }


}
