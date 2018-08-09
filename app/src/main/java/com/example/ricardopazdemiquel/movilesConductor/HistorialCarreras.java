package com.example.ricardopazdemiquel.movilesConductor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HistorialCarreras extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    public HistorialCarreras() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_historial_carreras, container, false);
        verViaje(93);

        return view;
    }
    private void verViaje(int id){
        Intent intent = new Intent(getActivity(),PerfilCarrera.class);
        intent.putExtra("id_carrera",id);
        startActivity(intent);
    }

}
