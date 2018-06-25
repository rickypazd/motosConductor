package com.example.ricardopazdemiquel.moviles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login extends AppCompatActivity {

    private Button btn_login_conductor;
    private Button btn_login_cliente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login_conductor=findViewById(R.id.btn_iniciar_conductor);
        btn_login_cliente=findViewById(R.id.btn_iniciar_cliente);

        btn_login_conductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,LoginConductor.class);
                startActivity(intent);
            }
        });

        btn_login_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,LoginCliente.class);
                startActivity(intent);
            }
        });
    }
}
