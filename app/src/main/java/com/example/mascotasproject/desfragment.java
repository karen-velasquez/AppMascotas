package com.example.mascotasproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;


public class desfragment extends AppCompatActivity {
    TextView mnombreMas, mCaractericas, mdatosperdida;
    ImageView mimagen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_desfragment);
        //ActionBar
        ActionBar actionBar=getSupportActionBar();
        //titulo action bar
        actionBar.setTitle("Detalles del Post");
        //seleccionar boton en action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mnombreMas=findViewById(R.id.nombreholder);
        mCaractericas=findViewById(R.id.caracteristicaholder);
        mdatosperdida=findViewById(R.id.perdidaholder);
        mimagen=findViewById(R.id.imageholder);

        //Obteniendo los datos del intent
        String mnombre=getIntent().getStringExtra("nombreMas");
        String mCaracteristicas=getIntent().getStringExtra("caracteristica");
        String mdatosper=getIntent().getStringExtra("perdida");
        String image=getIntent().getStringExtra("image");
       // byte[] bytes=getIntent().getByteArrayExtra("image");

     //   Bitmap bmp= BitmapFactory.decodeByteArray(bytes,0,bytes.length);

        //escoger data para views
        mnombreMas.setText(mnombre);
        mCaractericas.setText(mCaracteristicas);
        mdatosperdida.setText(mdatosper);
        Picasso.get().load(image).into(mimagen);


    }



}