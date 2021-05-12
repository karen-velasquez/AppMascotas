package com.example.mascotasproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MascotasAdapter extends RecyclerView.Adapter<MascotasAdapter.MyHolder>{

    Context context;

    List<model> mascotaList;

    public MascotasAdapter(Context context, List<model> mascotalist) {
            this.context=context;
            this.mascotaList=mascotalist;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.singlerowdesign,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //obteniendo datos
        String dnombreMas=mascotaList.get(position).getNombreMas();
        String dCaracteristicas=mascotaList.get(position).getCaracteristicas();
        String ddatosperdida=mascotaList.get(position).getUbicacionPerdida();
        String urlimagen=mascotaList.get(position).getImagen();

        //enviando data
        holder.mnombreMas.setText(dnombreMas);
        holder.mCaractericas.setText(dCaracteristicas);
        holder.mdatosperdida.setText(ddatosperdida);
        try{
            Picasso.get().load(urlimagen)
                    .into(holder.mimagen);
        }catch (Exception e){

        }

        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,desfragment.class);

                intent.putExtra("nombreMas",mascotaList.get(position).getNombreMas());
                intent.putExtra("caracteristica",mascotaList.get(position).getCaracteristicas());
                intent.putExtra("perdida",mascotaList.get(position).getUbicacionPerdida());
                intent.putExtra("image",mascotaList.get(position).getImagen());
                String quien = ((MostrarRecycler) context).getIntent().getStringExtra("quien");
                intent.putExtra("quien",quien);
                v.getContext().startActivity(intent);
                Toast.makeText(context,""+dnombreMas,Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mascotaList.size();
    }

    //view holder class
    class  MyHolder extends RecyclerView.ViewHolder{

        TextView mnombreMas, mCaractericas, mdatosperdida;
        ImageView mimagen;
        public MyHolder(@NonNull View itemView) {
          super(itemView);


              System.out.println("Llegue a este nivel numero 4..............");
              mnombreMas = itemView.findViewById(R.id.nombre);
              mCaractericas = itemView.findViewById(R.id.caracteristica);
              mdatosperdida = itemView.findViewById(R.id.datos_perdida);
              mimagen = itemView.findViewById(R.id.img1);



        }
    }
}
