package com.example.mascotasproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MostrarRecycler extends AppCompatActivity {
    private static final String TAG = "MostrarRecycler";
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    List<model> mascotaslist;
    MascotasAdapter mascotasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_recycler);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Lista de datos");

        //RecyclerView
        mRecyclerView=findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        //Colocando los valores del Firebase
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Mascotas/Datos");
         mAuth = FirebaseAuth.getInstance();
         if(getQuien().equals("Invitado")){
             mascotasrecyclerInvitados();
         }else{
             if(getQuien().equals("Usuario")){
                 mascotasrecyclerUsuarios(getCodigo());
             }
         }


    }

    public String getCodigo() {
        String codigo=getIntent().getStringExtra("codigo");
        System.out.println("numero 1quien es el codigo1"+codigo);
        return codigo;
    }
    public String getQuien() {
        String quien=getIntent().getStringExtra("quien");
        System.out.println("numero 1quien es el codigo1"+quien);
        return quien;
    }


    public void mascotasrecyclerUsuarios(String codigo){
        mascotaslist=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Mascotas/Datos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mascotaslist.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    /*obteniendo los datos de razas desde firebase*/
                    model modelmascotas=ds.getValue(model.class);

                    if(modelmascotas.getCodigoDueno().equals(codigo)){
                        //obtener todos los usuarios menos
                        mascotaslist.add(modelmascotas);

                    }
                }
                mascotasAdapter=new MascotasAdapter(MostrarRecycler.this,mascotaslist);
                mRecyclerView.setAdapter(mascotasAdapter);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    public void mascotasrecyclerInvitados(){
        mascotaslist=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Mascotas/Datos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mascotaslist.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    /*obteniendo los datos de razas desde firebase*/
                    model modelmascotas=ds.getValue(model.class);
                    String resultados[] = getIntent().getStringArrayExtra("resultados");
                    String raza1 = resultados[0];
                    String raza2 = resultados[1];

                    String razas = modelmascotas.getRazas();
                    String[] parts = razas.split("/");
                    String r1 = parts[0];
                    String r2 = parts[1];

                    if(r1.equals(raza1) || r2.equals(raza1) || r1.equals(raza2) || r2.equals(raza2)){
                        //obtener todos los usuarios menos
                        mascotaslist.add(modelmascotas);
                        System.out.print("LLEGUE AQUI PERO NOSE  COMO");

                    }
                }
                mascotasAdapter=new MascotasAdapter(MostrarRecycler.this,mascotaslist);
                mRecyclerView.setAdapter(mascotasAdapter);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



















  /*@Override
    protected void onStart() {


  }*/
    /*@Override
    protected void onStart() {
        System.out.println("Llegue a este nivel numero 2..............");
        super.onStart();
        String quien=getIntent().getStringExtra("quien");
        System.out.println("numero mostrar recycler"+quien);
            // do your stuff
        Query firebasesearchraza=mRef.orderByChild("nombreMas").startAt("firulais").endAt("firulais"+"\uf8ff");

            FirebaseRecyclerOptions<model> options =
                    new FirebaseRecyclerOptions.Builder<model>()
                            .setQuery(firebasesearchraza,model.class)
                            .build();

            FirebaseRecyclerAdapter<model, Holder> firebaseRecyclerAdapter=
                    new FirebaseRecyclerAdapter<model, Holder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull model model) {
                            holder.mnombreMas.setText(model.getNombreMas());
                            holder.mCaractericas.setText(model.getCaracteristicas());
                            holder.mdatosperdida.setText(model.getUbicacionPerdida());
                            Picasso.get().load(model.getImagen()).into(holder.mimagen);

                            holder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent=new Intent(MostrarRecycler.this,desfragment.class);
                                    intent.putExtra("nombreMas",model.getNombreMas());
                                    intent.putExtra("caracteristica",model.getCaracteristicas());
                                    intent.putExtra("perdida",model.getUbicacionPerdida());
                                    intent.putExtra("image",model.getImagen());
                                    System.out.println("QUE ESTOY ENVIANDOOOO"+quien);
                                    intent.putExtra("quien",quien);
                                    startActivity(intent);
                                }

                            });
                            holder.setOnLongClickListener(new View.OnLongClickListener(){
                                @Override
                                public boolean onLongClick(View view){

                                    AlertDialog.Builder builder=new AlertDialog.Builder(MostrarRecycler.this);
                                    String[]options={"Actualizar","Eliminar"};
                                    //escoger dialog
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //dependiendo de lo que escoja
                                            if(which==0){
                                                //click actualizar
                                                Intent intent=new Intent(MostrarRecycler.this,desfragment.class);
                                                intent.putExtra("nombreMas",(model.getNombreMas()).toString());
                                                intent.putExtra("caracteristica",model.getCaracteristicas().toString());
                                                intent.putExtra("perdida",model.getUbicacionPerdida().toString());
                                                intent.putExtra("image",model.getImagen().toString());
                                                startActivity(intent);

                                            }
                                            if(which==1){
                                                //click eliminar
                                                //llamar metodo
                                                showDeleteDataDialog(model.getNombreMas().toString(),model.getImagen().toString());
                                            }
                                        }
                                    });
                                    builder.create().show();

                                    return true;

                                }
                            });

                        }


                        @NonNull
                        @Override
                        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowdesign,parent,false);
                            Holder viewHolder=new Holder(view);
                            return viewHolder;
                        }


                    };
            firebaseRecyclerAdapter.startListening();
            mRecyclerView.setAdapter(firebaseRecyclerAdapter);




    }





    private void showDeleteDataDialog(String nombre, String image) {
        //Aler dialog
        AlertDialog.Builder builder= new AlertDialog.Builder(MostrarRecycler.this);
        builder.setTitle("Eliminar");
        builder.setMessage("Seguro que deseas eliminar este dato?");
        //obteniendo el boton positivo y negativo
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //el usuario presiona SI, se elimina los datos
                Query mQuery=mRef.orderByChild("NombreMas").equalTo(nombre);
                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(MostrarRecycler.this,"Datos eliminados correctamente",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //SI no se logra enviar mensaje de error
                        Toast.makeText(MostrarRecycler.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                mFirebaseDatabase=FirebaseDatabase.getInstance();
                mRef=mFirebaseDatabase.getReference("Mascotas/Datos");

                FirebaseStorage storage=FirebaseStorage.getInstance();
                StorageReference mPictureRefe= storage.getReferenceFromUrl(image);
                mPictureRefe.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //eliminacion correcta
                        Toast.makeText(MostrarRecycler.this,"Imagen eliminada correctamente", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //no se logra eliminar
                        Toast.makeText(MostrarRecycler.this,e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //si el usuario presion no
                dialog.dismiss();
            }
        });
        builder.create().show();

    }*/

    /*-------------------------------------------------------------------------------------------*/

   /* public static class Holder extends RecyclerView.ViewHolder {


        TextView mnombreMas, mCaractericas, mdatosperdida;
        ImageView mimagen;


        public Holder(@NonNull View itemView) {
            super(itemView);


            System.out.println("Llegue a este nivel numero 4..............");
            mnombreMas = itemView.findViewById(R.id.nombre);
            mCaractericas = itemView.findViewById(R.id.caracteristica);
            mdatosperdida = itemView.findViewById(R.id.datos_perdida);
            mimagen = itemView.findViewById(R.id.img1);

            //CON UN CLICK CORTO
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onClick(v);
                }
            });
            //EN CASO DE QUE EL CLICK SEA LARGO
           itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mClickLongListener.onLongClick(v);
                    return true;
                }
            });


        }

        private View.OnClickListener mClickListener;

        public void setOnClickListener(View.OnClickListener onClickListener) {
            mClickListener=onClickListener;
        }

        private View.OnLongClickListener mClickLongListener;

        public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
            mClickLongListener=onLongClickListener;
        }



    }
*/




}