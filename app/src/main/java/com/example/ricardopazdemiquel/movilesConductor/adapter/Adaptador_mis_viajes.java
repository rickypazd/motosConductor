package com.example.ricardopazdemiquel.movilesConductor.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ricardopazdemiquel.movilesConductor.Detalle_viaje_Cliente;
import com.example.ricardopazdemiquel.movilesConductor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class Adaptador_mis_viajes extends BaseAdapter {

    private JSONArray array;
    private Context contexto;
    private static final int EFECTIVO = 1;
    private static final int CREDITO = 2;

    public Adaptador_mis_viajes(Context contexto, JSONArray lista) {
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
                    .inflate(R.layout.layout_item_ultimos_viajes, viewGroup, false);
        }
        TextView text_fecha = view.findViewById(R.id.text_fecha);
        TextView text_auto = view.findViewById(R.id.text_auto);
        TextView text_inicio = view.findViewById(R.id.text_inicioViaje);
        TextView text_fin = view.findViewById(R.id.text_FinViaje);
        TextView text_tipo_pago = view.findViewById(R.id.text_tipoPago);
        TextView text_monto= view.findViewById(R.id.text_monto);
        ImageView btn_next = view.findViewById(R.id.btn_next);

        try {
            final JSONObject viajes = array.getJSONObject(i);
            double latinicial = viajes.getDouble("latinicial");
            double latfinal = viajes.getDouble("latfinal");
            double lnginicial = viajes.getDouble("lnginicial");
            double lngfinal = viajes.getDouble("lngfinal");
            double lat_final_real = viajes.getDouble("latfinalreal");
            double lng_final_real = viajes.getDouble("lngfinalreal");
            String id_carrera = viajes.getString("id_carrera");
            int estado= viajes.getInt("estado");
            int costo = viajes.getInt("costo_final");
            int tipo = viajes.getInt("tipo_pago");

            text_fecha.setText(viajes.getString("fecha_pedido").substring(0,16));
            text_auto.setText(viajes.getString("marca"));
            if(get_estado(estado)){
                text_inicio.setText(getCompleteAddressString(latinicial,lnginicial));
                text_fin.setText(getCompleteAddressString(lat_final_real,lng_final_real));
                text_monto.setText("bs. "+costo);
            }else if(!get_estado(estado)){
                text_inicio.setText(getCompleteAddressString(latinicial,lnginicial));
                text_fin.setText(getCompleteAddressString(latfinal,lngfinal));
                text_monto.setText("cancelado");
            }
            switch (tipo){
                case(EFECTIVO):
                    text_tipo_pago.setText("Efectivo");
                    break;
                case(CREDITO):
                    text_tipo_pago.setText("Credito");
                    break;
            }
            //text_tipo_pago.setText(viajes.getString(""));
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(contexto,Detalle_viaje_Cliente.class);
                    try {
                        intent.putExtra("id_carrera", viajes.getString("id_carrera"));
                        contexto.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


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

    private Boolean get_estado(int valor){
        switch (valor) {
            case 7:
                return true;
            case 10:
                return false;
        }
        return null;
    }

}
