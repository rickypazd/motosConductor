package com.example.ricardopazdemiquel.movilesConductor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ricardopazdemiquel.movilesConductor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;

import static android.content.Context.MODE_PRIVATE;

public class fragment_carrera_activa extends Fragment implements View.OnClickListener {

    private Switch sw_estandar;
    private Switch sw_togo;
    private Switch sw_maravilla;
    private Switch sw_super;

    private JSONObject usr_log;
    private JSONObject turno;

    public fragment_carrera_activa() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_carrera_activa, container, false);

        sw_estandar = view.findViewById(R.id.sw_estandar);
        sw_togo = view.findViewById(R.id.sw_togo);
        sw_maravilla = view.findViewById(R.id.sw_maravilla);
        sw_super = view.findViewById(R.id.sw_super_siete);
        sw_maravilla.setVisibility(View.GONE);
        if(getUsr_log()!=null){
            sw_estandar.setOnClickListener(this);
            sw_togo.setOnClickListener(this);
            sw_maravilla.setOnClickListener(this);
            sw_super.setOnClickListener(this);

            sw_estandar.setChecked(false);
            sw_togo.setChecked(false);
            sw_maravilla.setChecked(false);
            sw_super.setChecked(false);
            turno=((MainActivityConductor)getActivity()).getObj_turno();
            if(turno!=null){
                new obtener_activos().execute();

            }


        }
        return view;
    }

    public JSONObject getUsr_log() {
        SharedPreferences preferencias = getActivity().getSharedPreferences("myPref",MODE_PRIVATE);
        String usr = preferencias.getString("usr_log", "");
        if (usr.length()<=0) {

            Intent intent = new Intent(getActivity(),LoginConductor.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return null;
        }else{
            try {
                usr_log=new JSONObject(usr);
                return usr_log;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    @Override
    public void onClick(View view) {
        boolean b_estandar = sw_estandar.isChecked();
        boolean b_maravilla = sw_maravilla.isChecked();
        boolean b_super = sw_super.isChecked();
        boolean b_togo = sw_togo.isChecked();
        try {
            String resp=new buscar_carrera(b_estandar ,b_togo,b_maravilla,b_super).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private class buscar_carrera extends AsyncTask<Void, String, String> {

        private boolean estandar;
        private boolean togo;
        private boolean maravilla;
        private boolean ssuper;

        public buscar_carrera(boolean estandar, boolean togo, boolean maravilla, boolean ssuper) {
            this.estandar = estandar;
            this.togo = togo;
            this.maravilla = maravilla;
            this.ssuper = ssuper;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            Hashtable<String,String> param = new Hashtable<>();

            try {
                param.put("evento","update_tc_activo");
                param.put("id_usr",usr_log.getString("id"));
                param.put("estandar",estandar+"");
                param.put("togo",togo+"");
                param.put("maravilxla",maravilla+"");
                param.put("super", ssuper+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String pacientes) {
            super.onPostExecute(pacientes);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    private class obtener_activos extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            Hashtable<String,String> param = new Hashtable<>();
            try {
                param.put("evento","get_tc_activo");
                param.put("id_usr",usr_log.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String pacientes) {
            super.onPostExecute(pacientes);
            if(pacientes!=null){
                try {
                    JSONObject obj2 = new JSONObject(pacientes);
                    boolean exito = obj2.getBoolean("exito");
                    if (!exito) {
                        sw_estandar.setChecked(false);
                        sw_togo.setChecked(false);
                        sw_maravilla.setChecked(false);
                        sw_super.setChecked(false);
                    }else{
                        JSONObject obj=new JSONObject(pacientes);
                        if(turno.getInt("tipo")==1){
                            sw_super.setVisibility(View.VISIBLE);
                            boolean superr = obj.getBoolean("super");
                            if(sw_super.isChecked()!=superr){
                                sw_super.setChecked(superr);
                            }
                        }else{

                            if(obj2.getBoolean("act_estandar")){
                                sw_estandar.setVisibility(View.VISIBLE);
                            }
                            if(obj2.getBoolean("act_togo")){
                                sw_togo.setVisibility(View.VISIBLE);
                            }
                            if(obj2.getBoolean("act_maravilla")){
                               // sw_maravilla.setVisibility(View.VISIBLE);
                            }
                            if(obj2.getBoolean("act_supe")){
                               // sw_super.setVisibility(View.VISIBLE);
                            }

                            boolean estandar = obj.getBoolean("estandar");
                            if(sw_estandar.isChecked()!=estandar){
                                sw_estandar.setChecked(estandar);
                            }
                            boolean togo = obj.getBoolean("togo");
                            if(sw_togo.isChecked()!=togo){
                                sw_togo.setChecked(togo);
                            }
                            boolean maravilla = obj.getBoolean("maravilla");
                            if(sw_maravilla.isChecked()!=maravilla && usr_log.getString("sexo").equals("Mujer")){
                                sw_maravilla.setChecked(maravilla);
                            }
                            boolean superr = obj.getBoolean("super");
                            if(sw_super.isChecked()!=superr){
                                sw_super.setChecked(superr);
                            }

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getActivity(), "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

}
