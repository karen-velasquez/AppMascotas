package com.example.mascotasproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.mascotasproject.IA.ClassifierActivity;

public class loginnumero extends AppCompatActivity {

    EditText mloginnumero;
    Button btn_logincodigo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginnumero);

        mloginnumero=(EditText)findViewById(R.id.edit_logincodigo);
        btn_logincodigo=findViewById(R.id.btn_logincodigo);

        //Obteniendo los datos del intent
        String quien=getIntent().getStringExtra("quien");


        btn_logincodigo.setOnClickListener(v-> {
            Intent intent=new Intent(loginnumero.this, OpcionesUsuario.class);
            intent.putExtra("usuario",mloginnumero.getText().toString());
            intent.putExtra("quien",  quien);
            startActivity(intent);

        });

    }
}