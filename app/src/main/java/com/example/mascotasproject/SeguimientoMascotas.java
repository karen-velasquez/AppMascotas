package com.example.mascotasproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mascotasproject.IA.ClassifierActivity;
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
    ImageButton imageButton;


    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento_mascotas);
        ActionBar actionBar=getSupportActionBar();

        actionBar.setTitle("Seguimiento Mascotas");

        imageButton=findViewById(R.id.mapas);


        //RecyclerView
        mlocationRecyclerView=findViewById(R.id.locationRecyclerView);
        mlocationRecyclerView.setHasFixedSize(true);
        locationlist=new ArrayList<>();


        imageButton.setOnClickListener(v-> {
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        SeguimientoMascotas.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION_PERMISSION
                );
            } else {
                Intent intent=new Intent(SeguimientoMascotas.this,MapsActivity1.class);
                intent.putExtra("codigo",getcodigo());
                intent.putExtra("quien",getquien());
                intent.putExtra("codigoMascota",getIntent().getStringExtra("codMascota"));
                startActivity(intent);
            }

        });




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

    public String getquien(){
        String quien = getIntent().getStringExtra("quien");
        return quien;
    }
    public String getcodigo(){
        String codigo = getIntent().getStringExtra("codigo");
        return codigo;
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