package com.example.mascotasproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.mascotasproject.IA.CameraActivity;
import com.example.mascotasproject.IA.Classifier;
import com.example.mascotasproject.IA.ClassifierActivity;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class OpcionesIngreso extends AppCompatActivity {

    ImageButton musuario,minvitado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_ingreso);
        musuario=findViewById(R.id.usuario);
        minvitado=findViewById(R.id.invitado);


        musuario.setOnClickListener(v-> {
            Intent intent=new Intent(OpcionesIngreso.this,loginnumero.class);
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