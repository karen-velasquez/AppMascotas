package com.example.mascotasproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class desfragment extends AppCompatActivity {
    TextView mnombreMas, mCaractericas, mdatosperdida;
    ImageView mimagen;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
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
        String mcodDueno=getIntent().getStringExtra("codDueno");
        String mcodMascota=getIntent().getStringExtra("codMascota");
        String mnombre=getIntent().getStringExtra("nombreMas");
        String mCaracteristicas=getIntent().getStringExtra("caracteristica");
        String mdatosper=getIntent().getStringExtra("perdida");
        String image=getIntent().getStringExtra("image");
        String quien=getIntent().getStringExtra("quien");





        System.out.println("numero finalllllll"+quien);
        setButtonsVisibility(View.VISIBLE,quien);

       // byte[] bytes=getIntent().getByteArrayExtra("image");

     //   Bitmap bmp= BitmapFactory.decodeByteArray(bytes,0,bytes.length);

        //escoger data para views
        mnombreMas.setText(mnombre);
        mCaractericas.setText(mCaracteristicas);
        mdatosperdida.setText(mdatosper);
        Picasso.get().load(image).into(mimagen);


        mlocation.setOnClickListener(v-> {
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        desfragment.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION_PERMISSION
                );
            } else {
                Intent intent=new Intent(desfragment.this,MapsActivity1.class);
                intent.putExtra("codDueno",mcodDueno);
                intent.putExtra("codMascota",mcodMascota);
                intent.putExtra("nombreMas",mnombre);
                intent.putExtra("caracteristica",mCaracteristicas);
                intent.putExtra("perdida",mdatosper);
                intent.putExtra("image",image);
                intent.putExtra("quien",quien);
                startActivity(intent);
            }



        });



    }
    void setButtonsVisibility(final int visibility, String quien) {
        final boolean enabled = visibility == View.VISIBLE;
        if(quien.equals("Invitado")){
            mlocation.setVisibility(visibility);
            mlocation.setEnabled(enabled);
        }
        if(quien.equals("Usuario")){
        //    checkButtonuser.setVisibility(visibility);
        //    checkButtonuser.setEnabled(enabled);
        }
       // closeButton.setVisibility(visibility);
      //  closeButton.setEnabled(enabled);
        // saveButton.setVisibility(visibility);
        // saveButton.setEnabled(enabled);
    }



}