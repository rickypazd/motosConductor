package com.example.ricardopazdemiquel.movilesConductor.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ricardopazdemiquel.movilesConductor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class Adapter_transaccion extends BaseAdapter {

    private JSONArray array;
    private Context contexto;

    public Adapter_transaccion(Context contexto, JSONArray lista) {
        this.contexto = contexto;
        this.array = lista;
    }

    @Override
    public int getCount() {
        return array.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return array.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) { return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(contexto)
                    .inflate(R.layout.layout_item_transaccion, viewGroup, false);
        }
        TextView text_fecha = view.findViewById(R.id.text_fecha);
        TextView text_cantidad = view.findViewById(R.id.text_cantidad);
        TextView text_tipo = view.findViewById(R.id.text_tipo);

        try {
            final JSONObject trans = array.getJSONObject(i);
            int id = trans.getInt("id");
            String fecha = trans.getString("fecha");
            String tipo_nombre = trans.getString("tipo_nombre");
            String cantidad = trans.getString("cantidad");
            String id_usuario = trans.getString("id_usuario");
            String id_carrera = trans.getString("id_carrera");

            text_fecha.setText(fecha);
            text_cantidad.setText(cantidad);
            text_tipo.setText(tipo_nombre);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(contexto, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction addr", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction addr", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction addr", "Canont get Address!");
        }
        return strAdd;
    }


}
