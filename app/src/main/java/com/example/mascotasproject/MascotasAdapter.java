package com.example.mascotasproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MascotasAdapter extends RecyclerView.Adapter<MascotasAdapter.MyHolder>{

    //CARPETA DONDE SE ENCONTRARAN LAS IMAGENES
    String mStoragePath = "ImagenesMascotas/";

    //DIRECCION DE LA BASE DE DATOS PARA FIREBASE
    String mDatabasePath="Mascotas/Datos";

    //Creando el URI
    Uri mFilePathUri;


    //CREANDO EL REFERENCE DEL DATABASE Y STORAGE
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    FirebaseStorage mFirebaseStorage;


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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                String[] options={"Actualizar","Eliminar"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            Intent intent=new Intent(context,AddData.class);
                            intent.putExtra("codDueno",mascotaList.get(position).getCodigoDueno());
                            intent.putExtra("codMascota",mascotaList.get(position).getCodigoMascota());
                            intent.putExtra("nombreMas",mascotaList.get(position).getNombreMas());
                            intent.putExtra("caracteristica",mascotaList.get(position).getCaracteristicas());
                            intent.putExtra("perdida",mascotaList.get(position).getUbicacionPerdida());
                            intent.putExtra("image",mascotaList.get(position).getImagen());
                            intent.putExtra("codigo",((MostrarRecycler) context).getIntent().getStringExtra("codigo"));
                            intent.putExtra("quien",getquien());
                            intent.putExtra("funcion","update");
                            v.getContext().startActivity(intent);
                            ((MostrarRecycler)context).finish();

                          //  updateData(mascotaList.get(position));
                        }
                        if(which==1){
                            showDeleteDataDialog(mascotaList.get(position).getCodigoDueno(),mascotaList.get(position).getCodigoMascota(),mascotaList.get(position).getImagen());
                        }
                    }
                });
                builder.create().show();



                return false;
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

    private boolean deletePreviousImage(String imagen_url){
        final Boolean[] bandera = {false};
        StorageReference mPictureRef=FirebaseStorage.getInstance().getReferenceFromUrl(imagen_url);
        mPictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                bandera[0] =true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                bandera[0]=false;

            }
        });
        return bandera[0];
    }
    private void updateData(model model) {
        Boolean bandera=deletePreviousImage(model.getImagen());
        if(bandera==true){


        }

    }

    private void showDeleteDataDialog(String codigoDueno, String codigoMascota, String imagen_url) {
        //asignando la instancia de firebasestorage a un objeto de storage
        mStorageReference= FirebaseStorage.getInstance().getReferenceFromUrl(imagen_url);
        //asignando la instancia de direbasedatabase a el root con ese nombre
        mDatabaseReference= FirebaseDatabase.getInstance().getReference(mDatabasePath);

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Estas seguro de eliminar este post?");
        //en caso de si
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Mascotas/Datos");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            /*obteniendo los datos de razas desde firebase*/
                            String key=ds.getKey();
                            model modelmascotas = ds.getValue(model.class);

                            if (modelmascotas.getCodigoMascota().equals(codigoMascota) || modelmascotas.getCodigoDueno().equals(codigoDueno)) {
                                mStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context,"Imagen eliminada correctamente",Toast.LENGTH_SHORT).show();
                                        mDatabaseReference.child(key).removeValue();


                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });








            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context,"Opcion cancelada",Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
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
