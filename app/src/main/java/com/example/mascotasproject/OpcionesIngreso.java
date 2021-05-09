package com.example.mascotasproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.mascotasproject.IA.CameraActivity;
import com.example.mascotasproject.IA.Classifier;
import com.example.mascotasproject.IA.ClassifierActivity;

public class OpcionesIngreso extends AppCompatActivity {

    Button musuario,minvitado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_ingreso);
        musuario=findViewById(R.id.usuario);
        minvitado=findViewById(R.id.invitado);

        musuario.setOnClickListener(v-> {
            Intent intent=new Intent(OpcionesIngreso.this, ClassifierActivity.class);
            intent.putExtra("quien","Usuario");
            startActivity(intent);

        });

        minvitado.setOnClickListener(v-> {
            Intent intent=new Intent(OpcionesIngreso.this, ClassifierActivity.class);
            intent.putExtra("quien","Invitado");
            startActivity(intent);

        });

    }
}