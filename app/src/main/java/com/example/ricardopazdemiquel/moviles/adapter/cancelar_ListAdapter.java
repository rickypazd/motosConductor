package com.example.ricardopazdemiquel.moviles.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ricardopazdemiquel.moviles.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class cancelar_ListAdapter extends BaseAdapter {

    private JSONArray listaCanchas;
    private Context contexto;

    public cancelar_ListAdapter( Context contexto,JSONArray lista) {
        this.contexto = contexto;
        this.listaCanchas = lista;
    }

    @Override
    public int getCount() {
        return listaCanchas.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return listaCanchas.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(contexto)
                    .inflate(R.layout.layou_list_marca, viewGroup, false);
        }
        //ImageView codigo = view.findViewById(R.id.imgCancha);
        TextView nombre = view.findViewById(R.id.tv_NombreList);

        try {
            final JSONObject cancha = listaCanchas.getJSONObject(i);
            //imgCancha.setImageResource(cancha.getImagen());
            URL url = null;

            nombre.setText(cancha.getString("nombre"));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });




            /*btn_ver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inten = new Intent(contexto,detalleCancha.class);
                    try {
                        inten.putExtra("id_complejo",cancha.getInt("ID"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    contexto.startActivity(inten);
                }
            });*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }



}
