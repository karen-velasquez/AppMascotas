package com.example.mascotasproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        /*Si es usuario rellenar con datos de sus mascotas, si es invitados con datos de razas iguales*/
        if(quien.equals("Invitado")){
           // mascotasrecyclerInvitados();
        }else{
            if(quien.equals("Usuario")){
                String codigo=getIntent().getStringExtra("codigo");
                mascotasrecyclerUsuarios(codigo);
                System.out.print("que codigo INGRESOOOO"+codigo);
            }
        }



    }

    public void mascotasrecyclerUsuarios(String codigo){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Mascotas/Locaciones");
        System.out.print("ENTRO AQUI??????");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationlist.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    /*obteniendo los datos de razas desde firebase*/
                   locationMascota locationMascotas=ds.getValue(locationMascota.class);


                   System.out.print("cuales vioooooooooooo"+locationMascotas.getCodigoDueno());
                    if(locationMascotas.getCodigoDueno().equals(codigo)){
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












}