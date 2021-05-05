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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.FileNotFoundException;
import java.io.IOException;

public class AddData extends AppCompatActivity {
//hola probando
    EditText nombreAdd, perdidaAdd;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        //actionbar

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Adicionar un nuevo dato");

        nombreAdd = findViewById(R.id.nombreAdd);
        perdidaAdd = findViewById(R.id.perdidaAdd);
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



        //AL HACER CLICK EN EL BOTON
        mUploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){
                        uploadDatatoFirebase();
            }
        });
        //asignando la instancia de firebasestorage a un objeto de storage
        mStorageReference= FirebaseStorage.getInstance().getReference();
        //asignando la instancia de direbasedatabase a el root con ese nombre
        mDatabaseReference= FirebaseDatabase.getInstance().getReference(mDatabasePath);

        //progress dialog
        mProgressDialog=new ProgressDialog(AddData.this);



    }

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

                            mProgressDialog.dismiss();

                            String carac="holi";
                            String cdu="1";
                            String cmas="2";
                            String vige="true";
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
}