package com.example.ricardopazdemiquel.moviles;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdaptadorSieteEstandar extends RecyclerView.Adapter<viewHolder> {

    private JSONArray listaCanchas;
    private Context contexto;
    private PedirSieteMap pm;

    public AdaptadorSieteEstandar(JSONArray listaCanchas, Context contexto, PedirSieteMap pm) {
            this.listaCanchas = listaCanchas;
        this.contexto = contexto;
        this.pm =pm;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_siete_estandar, parent, false);
        viewHolder holder = new viewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, int position) {
        try {

            JSONObject obj =listaCanchas.getJSONObject(position);
            holder.btn_reservar.setText(obj.getString("nombre"));
            holder.btn_reservar.setTag(obj.getInt("id"));
            holder.btn_reservar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        int id = (int) view.getTag();
                        pm.calculando_ruta(view ,id);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return listaCanchas.length();
    }
}
