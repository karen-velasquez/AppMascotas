package com.example.mascotasproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;


public class desfragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    String NombreMas,Caracteristicas,UbicacionPerdida,Imagen;

    public desfragment() {

    }

    public desfragment(String NombreMas,String Caracteristicas,String UbicacionPerdida,String Imagen) {
        this.NombreMas=NombreMas;
        this.Caracteristicas=Caracteristicas;
        this.UbicacionPerdida=UbicacionPerdida;
        this.Imagen=Imagen;
    }

   public static desfragment newInstance(String param1, String param2) {
        desfragment fragment = new desfragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_desfragment, container, false);
        ImageView image=view.findViewById(R.id.imageholder);
        TextView nombre=view.findViewById(R.id.nombreholder);
        TextView caracteristicas=view.findViewById(R.id.caracteristicaholder);
        TextView datos_perdida=view.findViewById(R.id.perdidaholder);

        nombre.setText(NombreMas);
        caracteristicas.setText(Caracteristicas);
        datos_perdida.setText(UbicacionPerdida);
        Glide.with(getContext()).load(Imagen).into(image);

        return view;
    }

    public void onBackPressed(){
        AppCompatActivity activity=(AppCompatActivity)getContext();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.wrapper,new recfragment()).addToBackStack(null).commit();
    }
}