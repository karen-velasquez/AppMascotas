package com.example.mascotasproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class MostrarRecycler extends AppCompatActivity {

    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_recycler);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Lista de datos");
        System.out.println("Llegue a este nivel numero 1..............");

        //RecyclerView
        mRecyclerView=findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        //colocando el layout en el linearlayout
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Mascotas/Datos");


    }


    @Override
    protected void onStart() {
        System.out.println("Llegue a este nivel numero 2..............");
        super.onStart();
        FirebaseRecyclerOptions<model> options =
                new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(mRef,model.class)
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
                                Intent intent=new Intent(view.getContext(),desfragment.class);
                                intent.putExtra("nombreMas",model.getNombreMas());
                                intent.putExtra("caracteristica",model.getCaracteristicas());
                                intent.putExtra("perdida",model.getUbicacionPerdida());
                                intent.putExtra("image",model.getImagen());
                                startActivity(intent);

                              /*  TextView mnombreMas=view.findViewById(R.id.nombre);
                                TextView mCaractericas=view.findViewById(R.id.caracteristica);
                                ImageView mimagen=view.findViewById(R.id.img1);
                                TextView mdatosperdida=view.findViewById(R.id.datos_perdida);
                                //Obtener data para views
                                String mnombre=mnombreMas.getText().toString();
                                String mCaracteristicas=mCaractericas.getText().toString();
                                String mdatosper=mdatosperdida.getText().toString();
                                String urlimage=mimagen.getTes(model.getImagen());
                                Drawable mDrawable=mimagen.getDrawable();
                                Bitmap mbitmap= ((BitmapDrawable)mDrawable).getBitmap();

                              */

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
        RecyclerView mRecyclerView=(RecyclerView) this.findViewById(R.id.mRecyclerView);
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    /*-------------------------------------------------------------------------------------------*/

    public static class Holder extends RecyclerView.ViewHolder {

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
          /*  itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mClickListener.onItemLongClick(v);
                    return true;
                }
            });*/


        }

        private View.OnClickListener mClickListener;

        public void setOnClickListener(View.OnClickListener onClickListener) {
            mClickListener=onClickListener;
        }

        //interface para enviar los callbacks
        public interface OnClickListener{
            void onItemClick(View view, int position);
            void onItemLongClick(View view,int position);


        }


    }





}