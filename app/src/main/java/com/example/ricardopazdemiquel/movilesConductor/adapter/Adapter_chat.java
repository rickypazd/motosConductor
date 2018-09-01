package com.example.ricardopazdemiquel.movilesConductor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.ricardopazdemiquel.movilesConductor.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Adapter_chat extends BaseAdapter {

    private Context contexto;
    private JSONArray array = new JSONArray();
    private int miId;
    MenuItem item;

    public Adapter_chat(Context contexto, JSONArray array, int miId) {
        this.contexto = contexto;
        this.array = array;
        this.miId=miId;
    }

    public JSONArray getArray(){
        return array;
    }

    @Override
    public int getCount() {
        return array.length();
    }

    @Override
    public JSONObject getItem(int i) {
        try {
            return array.getJSONObject(i);
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


        try {
            JSONObject obj =  array.getJSONObject(i);
            if(obj.getInt("id_emisor")==miId){
                    view = LayoutInflater.from(contexto).inflate(R.layout.layout_item_chat_e, viewGroup, false);
            }else{
                view = LayoutInflater.from(contexto).inflate(R.layout.layout_item_chat, viewGroup, false);
            }
            TextView text_mensaje = view.findViewById(R.id.text_mensaje);
            text_mensaje.setText(obj.getString("mensaje"));
            view.setTag(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void addItem(JSONObject obj){
        if(array!=null){
            array.put(obj);
        }
    }

    public void removeiten(int pos){
        if(array!=null){
            array.remove(pos);
        }
    }

    public void updateItem(JSONObject obj ,int pos){
        if(array!=null){
            try {
                array.put(pos,obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
