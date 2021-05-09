package com.example.mascotasproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class desfragment extends AppCompatActivity {
    TextView mnombreMas, mCaractericas, mdatosperdida;
    ImageView mimagen;
    ImageButton mlocation;

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
        mlocation=findViewById(R.id.locationButton);

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


        mlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(desfragment.this, GetLocation.class);
                startActivity(intent);
            }
        });

    }



}