package com.example.mascotasproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mascotasproject.IA.ClassifierActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class AddData extends AppCompatActivity {
//hola probando
    EditText nombreAdd, perdidaAdd, caracteristicaAdd;
    ImageView imagenAdd;
    Button mUploadBtn;

    //CARPETA DONDE SE ENCONTRARAN LAS IMAGENES
    String mStoragePath = "ImagenesMascotas/";

    //DIRECCION DE LA BASE DE DATOS PARA FIREBASE
    String mDatabasePath="Mascotas/Datos";

    //Creando el URI
    Uri mFilePathUri;


    //CREANDO EL REFERENCE DEL DATABASE Y STORAGE
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    //ProgressDialog
    ProgressDialog mProgressDialog;

    //Code de requisito para escoger imagen
    int IMAGE_REQUEST_CODE=5;

    //Datos para el stored
    String mnombreUp,mperdidaUp,mimagenUp,mcaracUp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        //actionbar

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Adicionar un nuevo dato");

        nombreAdd = findViewById(R.id.nombreAdd);
        perdidaAdd = findViewById(R.id.perdidaAdd);
        caracteristicaAdd=findViewById(R.id.caracteristicaAdd);
        imagenAdd=findViewById(R.id.imagenAdd);
        mUploadBtn=findViewById(R.id.buttonupload);




        //AL TOCAR LA IMAGEN
        imagenAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Seleccionar imagen"),IMAGE_REQUEST_CODE);


            }
        });





        //Obteniendo los datos del Intent
        Bundle intent=getIntent().getExtras();
        if(intent!=null){
            mnombreUp=intent.getString("nombreMas");
            mperdidaUp=intent.getString("perdida");
            mcaracUp=intent.getString("caracteristica");
            mimagenUp=intent.getString("image");
            nombreAdd.setText(mnombreUp);
            perdidaAdd.setText(mperdidaUp);
            Picasso.get().load(mimagenUp).into(imagenAdd);
            actionBar.setTitle("Actualizar post");
            mUploadBtn.setText("Actualizar");

        }


        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDatatoFirebase();
            }
        });

        //AL HACER CLICK EN EL BOTON
     /*  mUploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mUploadBtn.getText().equals("Upload")){

                }else {
                    beginUpdate();
                }


            }
        });*/
        //asignando la instancia de firebasestorage a un objeto de storage
        mStorageReference= FirebaseStorage.getInstance().getReference();
        //asignando la instancia de direbasedatabase a el root con ese nombre
        mDatabaseReference= FirebaseDatabase.getInstance().getReference(mDatabasePath);

        //progress dialog
        mProgressDialog=new ProgressDialog(AddData.this);

    }

    private void beginUpdate() {
        mProgressDialog.setMessage("Actualizando....");
        mProgressDialog.show();
        deletePreviousImage();

    }

    private void deletePreviousImage() {
        FirebaseStorage storage= FirebaseStorage.getInstance();
        StorageReference mPictureRef=storage.getReferenceFromUrl(mimagenUp);
        mPictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //eliminando

                Toast.makeText(AddData.this,"Eliminada imagen antigua....",Toast.LENGTH_SHORT).show();
                uploadNewImage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //error al eliminar
                Toast.makeText(AddData.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void uploadNewImage() {
        String imagename=System.currentTimeMillis()+".png";
        StorageReference storageReference=mStorageReference.child(mStoragePath+imagename);
        Bitmap bitmap= ((BitmapDrawable)imagenAdd.getDrawable()).getBitmap();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        //compress image
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] data=baos.toByteArray();
        UploadTask uploadTask=storageReference.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddData.this,"Nueva imagen actualizada",Toast.LENGTH_SHORT).show();
                //sacando la url de storage
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isSuccessful());
                Uri url = uri.getResult();
                //actualizando en la BD
                updateDatabase(url.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddData.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void updateDatabase(final String s) {
        //actualizando valores
        String nombre=nombreAdd.getText().toString();
        String perdida=perdidaAdd.getText().toString();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference mRef=firebaseDatabase.getReference("Mascotas/Datos");
        Query query=mRef.orderByChild("NombreMas").equalTo(nombre);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //actualizando datos
                for(DataSnapshot ds:snapshot.getChildren()){
                    //actualizando
                    ds.getRef().child("NombreMas").setValue(nombre);
                    ds.getRef().child("ubicacionPerdida").setValue(perdida);
                    ds.getRef().child("imagen").setValue(s);
                }
                mProgressDialog.dismiss();
                Toast.makeText(AddData.this,"Datos actualizados",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddData.this,MostrarRecycler.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    /*-------------------------------SE SUBE LOS NUEVOS DATOS A FIREBASE CUANDO SE SELECCIONO UNA IMAGEN------------------------------------------------------------*/
    private void uploadDatatoFirebase(){
        //verificando que filepathyuri esta vacio o no
        if(mFilePathUri!=null){
            mProgressDialog.setTitle("Cargando...");
            mProgressDialog.show();
            StorageReference storageReference2nd=mStorageReference.child(mStoragePath+System.currentTimeMillis()+"."+getFileExtension(mFilePathUri));

            //adicionando un suceso a storagereference2nd
            storageReference2nd.putFile(mFilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String mnombreAdd=nombreAdd.getText().toString().trim();
                            String mperdidaAdd=perdidaAdd.getText().toString().trim();
                            String mcaracteristicaAdd=caracteristicaAdd.getText().toString().trim();
                            String mcoddueno=getCodigo();
                            String mcodmascota=codigoMascotaGen(10);
                            String mvigencia="true";
                            String mraza="Pastor Aleman";

                            mProgressDialog.dismiss();

                            //sacando la url de storage
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();

                            Toast.makeText(AddData.this,"Cargando succeso",Toast.LENGTH_SHORT).show();
                            model modelo=new model(mnombreAdd, mperdidaAdd, url.toString(),carac,cdu,cmas,vige);
                            //obteniendo el id de la imagen subida
                            String imageUploadId= mDatabaseReference.push().getKey();
                            //adicionando la imagen cargada a los id's de los elementos hijos dentro databasereference
                            mDatabaseReference.child(imageUploadId).setValue(modelo);
                        }
                    })
                    //por si existe al error en la red al subir
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(AddData.this,e.getMessage(),Toast.LENGTH_SHORT);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            mProgressDialog.setTitle("Cargando....");
                        }
                    });
        }
        else {
            Toast.makeText(this,"Por favor selecciona una imagen",Toast.LENGTH_SHORT).show();
        }
    }



    public void showDeleteDataDialog(String currentTitle,String currentImage){
        AlertDialog.Builder builder=new AlertDialog.Builder(AddData.this);
        builder.setTitle("Eliminar");
        builder.setMessage("Estas seguro que deseas eliminar esta publicacion?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cuando el usuario presiona SI , elimina los datos
                Query mQuery = mDatabaseReference.orderByChild("nombreMas").equalTo(currentTitle);
                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        //Mensaje despues de eliminar los datos
                        Toast.makeText(AddData.this, "Se elimino correctamente el dato", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Si nada sucede esto envia el  mensaje de error
                        Toast.makeText(AddData.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                //Eliminando la imagen usando de referencias la url from FirebaseStorage
                StorageReference mPicturRefe = FirebaseStorage.getInstance().getReferenceFromUrl(currentImage);
                mPicturRefe.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddData.this, "Imagen eliminada", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cuando el usuario presion NO, desaparece el dialogo
                dialog.dismiss();

            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST_CODE
            && resultCode==RESULT_OK
            && data !=null
            && data.getData()!=null){
            mFilePathUri=data.getData();

            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),mFilePathUri);
                imagenAdd.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }






    /*Comprobando y uniendo Generador Random y no repcodmascota*/
    public static String codigoMascotaGen(int i){
        boolean flag=false;
        String generadorrandom="";
        while (flag==false){
            generadorrandom=GenerandoRandom(i);
            if(noRepCodMascota(generadorrandom)==true){
                flag=false;
            }else{
                flag=true;
            }
        }
        return generadorrandom;
    }


    /*GENERANDO UN STRING RANDOMICOOOOOOOOOOOOOOOOO-------------------------------------------------------------*/
    public static String GenerandoRandom(int i){
        final String characters="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result=new StringBuilder();
        while(i>0){
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            i--;
        }

        return result.toString();
    }

    /*REVISANDO QUE NINGUNA OTRA MASCOTA TENGA EL MISMO CODIGOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO----------------------*/
    public static boolean noRepCodMascota(String codigomascosta) {
        final boolean[] flag = {false};
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Mascotas/Datos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    /*obteniendo los datos de razas desde firebase*/
                    model modelmascotas = ds.getValue(model.class);
                    if (modelmascotas.getCodigoMascota().equals(codigomascosta)) {
                        flag[0] =true;
                    } else {
                        flag[0] =false;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return flag[0];
    }


    /*Obteniendo el quien y el codigo-------------------------------------*/
    public String getQuien(){
        String quien=getIntent().getStringExtra("quien");
        return quien;
    }
    public String getCodigo(){
        String codigo=getIntent().getStringExtra("codigo");
        return codigo;
    }

}