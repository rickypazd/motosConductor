package com.example.ricardopazdemiquel.moviles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;

public class activity_pedirSiete extends AppCompatActivity {

private Button btn_listo;
private Spinner sp_tipo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedir_siete);
        sp_tipo=findViewById(R.id.sp_tipo);
        sp_tipo.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,new String[]{"ESTANDAR","COND. MUJER","PEDIDO"}));
        btn_listo=findViewById(R.id.btn_listo_buscar);
        btn_listo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                JSONArray arr = new JSONArray();
                String jsn="";
                intent.putExtra("Data","Hola ricky");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
