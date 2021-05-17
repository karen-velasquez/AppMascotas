package com.example.mascotasproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

        /*Que el boton de ir a ver locaciones aparezca en caso de que quien sea Usuario*/
        final boolean enabled = View.VISIBLE ==View.VISIBLE ;
        if(getquien().equals("Usuario")){
            holder.muser_maslocation.setVisibility(View.VISIBLE);
            holder.muser_maslocation.setEnabled(enabled);
        }

        //obteniendo datos para el card view dentro el Recycler View
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
            Toast.makeText(context, "Hay un error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }

        //en caso de que se clickee un item del RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,desfragment.class);

                intent.putExtra("codDueno",mascotaList.get(position).getCodigoDueno());
                intent.putExtra("codMascota",mascotaList.get(position).getCodigoMascota());
                intent.putExtra("nombreMas",mascotaList.get(position).getNombreMas());
                intent.putExtra("caracteristica",mascotaList.get(position).getCaracteristicas());
                intent.putExtra("perdida",mascotaList.get(position).getUbicacionPerdida());
                intent.putExtra("image",mascotaList.get(position).getImagen());
                intent.putExtra("quien",getquien());
                v.getContext().startActivity(intent);
                Toast.makeText(context,""+dnombreMas,Toast.LENGTH_SHORT).show();

            }
        });

        holder.muser_maslocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,SeguimientoMascotas.class);
                intent.putExtra("codDueno",mascotaList.get(position).getCodigoDueno());
                intent.putExtra("codMascota",mascotaList.get(position).getCodigoMascota());
                intent.putExtra("nombreMas",mascotaList.get(position).getNombreMas());
                intent.putExtra("caracteristica",mascotaList.get(position).getCaracteristicas());
                intent.putExtra("perdida",mascotaList.get(position).getUbicacionPerdida());
                intent.putExtra("image",mascotaList.get(position).getImagen());
                intent.putExtra("quien",getquien());
                intent.putExtra("codigo",((MostrarRecycler) context).getIntent().getStringExtra("codigo"));
                v.getContext().startActivity(intent);
                Toast.makeText(context,""+dnombreMas,Toast.LENGTH_SHORT).show();
            }
        });





    }

    public String getquien(){
        String quien = ((MostrarRecycler) context).getIntent().getStringExtra("quien");
        return quien;
    }

    @Override
    public int getItemCount() {
        return mascotaList.size();
    }

    //view holder class
    class  MyHolder extends RecyclerView.ViewHolder{

        TextView mnombreMas, mCaractericas, mdatosperdida;
        ImageView mimagen;
        ImageButton muser_maslocation;
        public MyHolder(@NonNull View itemView) {
          super(itemView);

              mnombreMas = itemView.findViewById(R.id.nombre);
              mCaractericas = itemView.findViewById(R.id.caracteristica);
              mdatosperdida = itemView.findViewById(R.id.datos_perdida);
              mimagen = itemView.findViewById(R.id.img1);
              muser_maslocation=itemView.findViewById(R.id.usuario_maslocation);



        }
    }
}
