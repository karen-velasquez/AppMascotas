package com.example.mascotasproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class myadapter extends FirebaseRecyclerAdapter<model,myadapter.myviewholder>  {


    public myadapter(@NonNull FirebaseRecyclerOptions<model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull model model) {
        holder.nombre.setText(model.getNombreMas());
        holder.caracteristica.setText(model.getCaracteristicas());
        holder.datos_perdida.setText(model.getUbicacionPerdida());
        Glide.with(holder.img1.getContext()).load(model.getImagen()).into(holder.img1);
        holder.img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity=(AppCompatActivity)v.getContext();
               // activity.getSupportFragmentManager().beginTransaction().replace(R.id.wrapper,new desfragment(model.getNombreMas(),model.getCaracteristicas(),model.getUbicacionPerdida(),model.getImagen())).addToBackStack(null).commit();
            }
        });

       /* holder.img1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String currentTitle=model.getNombreMas();
                System.out.println("el nombre   "+currentTitle);
                String currentImage=model.getImagen();
                System.out.println("el nombre   "+currentImage);
                addData.showDeleteDataDialog(currentTitle,currentImage);
                return true;
            }
        });*/

    }



    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowdesign,parent,false);
       return new myviewholder(view);

    }

    public class myviewholder extends RecyclerView.ViewHolder
    {
        ImageView img1;
        TextView nombre, caracteristica, datos_perdida;

        public myviewholder(@NonNull View itemView){
            super(itemView);

            img1=itemView.findViewById(R.id.img1);
            nombre=itemView.findViewById(R.id.nombre);
            caracteristica=itemView.findViewById(R.id.caracteristica);
            datos_perdida=itemView.findViewById(R.id.datos_perdida);
        }
    }









}
