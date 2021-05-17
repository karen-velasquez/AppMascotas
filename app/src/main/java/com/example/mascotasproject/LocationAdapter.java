package com.example.mascotasproject;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyHolder>{


    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    Context context;

    List<locationMascota> locationList;

    public LocationAdapter(Context context, List<locationMascota> locationList) {
        this.context=context;
        this.locationList=locationList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.location_mas_user,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        /*Que el boton de ir a ver locaciones aparezca en caso de que quien sea Usuario*/


        //obteniendo datos
        String dnombreMas=locationList.get(position).getNombreMas();
        String dCaracteristica=locationList.get(position).getCaracteristica();
        String dlatitude=locationList.get(position).getLatitude();
        String dlongitud=locationList.get(position).getLongitud();


        /*ingresando los datos al holder---------------------*/
        holder.mvisto_Location.setText(dnombreMas);
        holder.mdireccion_location.setText(dCaracteristica);
        holder.mlatitude_location.setText(dlatitude);
        holder.mlongitud_location.setText(dlongitud);

        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               /* Intent intent=new Intent(context,desfragment.class);

                intent.putExtra("nombreMas",mascotaList.get(position).getNombreMas());
                intent.putExtra("caracteristica",mascotaList.get(position).getCaracteristicas());
                intent.putExtra("perdida",mascotaList.get(position).getUbicacionPerdida());
                intent.putExtra("image",mascotaList.get(position).getImagen());
                intent.putExtra("quien",getquien());
                v.getContext().startActivity(intent);
                Toast.makeText(context,""+dnombreMas,Toast.LENGTH_SHORT).show();*/

            }
        });

        holder.mmapa_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,MapsActivity1.class);
                intent.putExtra("codigo",getcodigo());
                intent.putExtra("quien",getquien());
                intent.putExtra("codigoMascota", ((SeguimientoMascotas) context).getIntent().getStringExtra("codMascota"));
                v.getContext().startActivity(intent);

               // Toast.makeText(context,""+dnombreMas,Toast.LENGTH_SHORT).show();
            }
        });





    }

    public String getquien(){
        String quien = ((SeguimientoMascotas) context).getIntent().getStringExtra("quien");
        return quien;
    }
    public String getcodigo(){
        String codigo = ((SeguimientoMascotas) context).getIntent().getStringExtra("codigo");
        return codigo;
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    //view holder class
    class  MyHolder extends RecyclerView.ViewHolder{

        TextView mvisto_Location,mlatitude_location,mlongitud_location,mdireccion_location;

        ImageButton mmapa_location;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mvisto_Location = itemView.findViewById(R.id.visto_location);
            mlatitude_location = itemView.findViewById(R.id.latitud_location);
            mlongitud_location = itemView.findViewById(R.id.longitud_location);
            mdireccion_location=itemView.findViewById(R.id.direccion_location);

            mmapa_location=itemView.findViewById(R.id.mapa_location);



        }
    }
}
