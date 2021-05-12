package com.example.mascotasproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.mascotasproject.IA.ClassifierActivity;

public class OpcionesUsuario extends AppCompatActivity {

    Button mbtn_seperdio, mbtn_seguimiento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_usuario);
        mbtn_seperdio=findViewById(R.id.btn_seperdio);
        mbtn_seguimiento=findViewById(R.id.btn_seguimiento);


        String usuario=getIntent().getStringExtra("usuario");
        String quien=getIntent().getStringExtra("quien");


        mbtn_seperdio.setOnClickListener(v-> {
            Intent intent=new Intent(OpcionesUsuario.this, AddData.class);
            intent.putExtra("usuario",usuario);
            intent.putExtra("quien",  quien);
            startActivity(intent);
        });


        mbtn_seguimiento.setOnClickListener(v->{
            Intent intent=new Intent(OpcionesUsuario.this, SeguimientoMascotas.class);
            intent.putExtra("codigo",usuario);
            intent.putExtra("quien",  quien);
            startActivity(intent);
        });

    }
}