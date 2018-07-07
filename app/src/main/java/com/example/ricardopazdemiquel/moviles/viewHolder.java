package com.example.ricardopazdemiquel.moviles;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class viewHolder extends RecyclerView.ViewHolder {


    public Button btn_reservar;
    public viewHolder(View v) {

        super(v);

         btn_reservar= v.findViewById(R.id.btn_reservar);

    }
}
