package com.example.ricardopazdemiquel.moviles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Elejir_tipo_siete extends AppCompatActivity implements View.OnClickListener{


    private Button siete;
    private Button siete_maravilla;
    private Button siete_super;
    private Button siete_togo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elejir_tipo_siete);

        siete = findViewById(R.id.btn_siete);
        siete_maravilla = findViewById(R.id.btn_sieteMaravilla);
        siete_super = findViewById(R.id.btn_superSiete);
        siete_togo = findViewById(R.id.btn_sietetogo);

        siete.setOnClickListener(this);
        siete_maravilla.setOnClickListener(this);
        siete_super.setOnClickListener(this);
        siete_togo.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_siete:
                break;
            case R.id.btn_sieteMaravilla:
                break;
            case R.id.btn_superSiete:
                break;
            case R.id.btn_sietetogo:
                break;
        }
    }
}
