package com.example.mascotasproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SeguimientoMascotas extends AppCompatActivity {

    RecyclerView mlocationRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseAuth mAuth;


    List<locationMascota> locationlist;
    LocationAdapter locationAdapter;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento_mascotas);
        ActionBar actionBar=getSupportActionBar();

        actionBar.setTitle("Seguimiento Mascotas");


        //RecyclerView
        mlocationRecyclerView=findViewById(R.id.locationRecyclerView);
        mlocationRecyclerView.setHasFixedSize(true);
        locationlist=new ArrayList<>();


        //Colocando los valores del Firebase
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Mascotas/Locaciones");
        mAuth = FirebaseAuth.getInstance();

        /*Obteniendo el intent de quien es, si usuario o invitado*/
        String quien=getIntent().getStringExtra("quien");


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalTime horaActual = LocalTime.now();
            System.out.print("hora actual" + horaActual);
            LocalDate fechaActual = null;

            fechaActual = LocalDate.now();

            System.out.print("fecha actual" + fechaActual);

            LocalDateTime fechaHora = LocalDateTime.now();
            System.out.print("fecaha y hora" + fechaHora);
        }


        /*Si es usuario rellenar con datos de sus mascotas, si es invitados con datos de razas iguales*/
        if(quien.equals("Invitado")){
           // mascotasrecyclerInvitados();
        }else{
            if(quien.equals("Usuario")){
                String codigo=getIntent().getStringExtra("codigo");
                String codMascota=getIntent().getStringExtra("codMascota");
                positionrecyclerUsuarios(codigo,codMascota);
            }
        }



    }


    public void positionrecyclerUsuarios(String codigo, String codMascota){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Mascotas/Locaciones");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationlist.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    /*obteniendo los datos de razas desde firebase*/
                   locationMascota locationMascotas=ds.getValue(locationMascota.class);
                    if(locationMascotas.getCodigoDueno().equals(codigo) && locationMascotas.getCodigoMascota().equals(codMascota)){
                        //obtener todos los usuarios menos
                        locationlist.add(locationMascotas);

                    }
                }
                locationAdapter=new LocationAdapter(SeguimientoMascotas.this,locationlist);
                mlocationRecyclerView.setAdapter(locationAdapter);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    /*Obteniendo Fecha y Hora------------------*/


    public void obtenerfechahora(){


    }














}