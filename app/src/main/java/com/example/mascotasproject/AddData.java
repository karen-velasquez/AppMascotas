package com.example.mascotasproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mascotasproject.IA.Classifier;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AddData extends AppCompatActivity {

    // mobilenet: 224, inception_v3: 299
    private static final int INPUT_SIZE = 299;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128;
    // mobilenet: input, inception_v3: Mul, Keras: input_1
    private static final String INPUT_NAME = "Mul";
    // output, inception(tf): final_result, Keras: output/Softmax
    private static final String OUTPUT_NAME = "final_result";
    private static final String MODEL_FILE = "file:///android_asset/stripped.pb";
    protected AddData.InferenceTask inferenceTask;
    private Classifier classifier;
    protected ArrayList<String> currentRecognitions;
    boolean imageSet = false;


    private static final int PERMISSION_CODE=1001;
    private static final int IMAGE_PICK_CODE=1000;




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

    String urlimagen;


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



        /*Obteniendo el codigo del usuario y que tipo de usuario es*/
        String usuario=getIntent().getStringExtra("codigo");
        String quien=getIntent().getStringExtra("quien");
        urlimagen=getIntent().getStringExtra("urlimagen");
//        Log.d("OBTUVE ESTA URL",urlimagen);
        Log.d("quien es",quien);
       // Picasso.get().load(urlimagen).into(imagenAdd);


        //AL TOCAR LA IMAGEN
        imagenAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==
                        PackageManager.PERMISSION_DENIED){
                    String[] permission= {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permission,PERMISSION_CODE);

                }else {
                    pickImageFromGallery();
                }
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
            mUploadBtn.setText("SUBIR POST");

        }


        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDatatoFirebase();

                Intent intent=new Intent(AddData.this, OpcionesUsuario.class);
                intent.putExtra("usuario",getCodigo());
                intent.putExtra("quien",  getQuien());
                startActivity(intent);

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

                            /*Obteniendo las razas*/
                            // String resultados[] = getIntent().getStringArrayExtra("resultados");
                            String raza1 = currentRecognitions.get(0);
                            String raza2 = currentRecognitions.get(1);

                            if(raza2==null){
                                raza2=raza1;
                            }
                            Log.d("las razas son",raza1+raza2);
                            //sacando la url de storage
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();



                            if((mnombreAdd.isEmpty() || mperdidaAdd.isEmpty() || url.toString().isEmpty()|| mcaracteristicaAdd.isEmpty())!=true){
                                model modelo=new model(mnombreAdd, mperdidaAdd, url.toString() ,mcaracteristicaAdd,mcoddueno,mcodmascota,mvigencia,raza1+"/"+raza2);
                                //obteniendo el id de la imagen subida
                                Toast.makeText(AddData.this,url.toString()+"ESETE ES EL URL",Toast.LENGTH_SHORT).show();
                                String imageUploadId= mDatabaseReference.push().getKey();
                                //adicionando la imagen cargada a los id's de los elementos hijos dentro databasereference
                                mDatabaseReference.child(imageUploadId).setValue(modelo);
                                Toast.makeText(AddData.this,"Post cargado correctamente",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddData.this,"Todos los datos deben estar llenos",Toast.LENGTH_SHORT).show();
                            }

                           mProgressDialog.dismiss();


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










    /*-----------------------------------ES RESPECTO A ANALIZAR LA IMAGEN QUE SUBIRA-----------------*/

    /*--------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if(grantResults.length>0 && grantResults[0]==
                        PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }else{
                    Toast.makeText(this,"No se tiene permiso",Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void pickImageFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }


    /**
     * user has chosen a picture from the image gallery
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //     super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE)
            imagenAdd.setImageURI(data.getData());
            classifyLoadedImage(data.getData());
            mFilePathUri=data.getData();



    }


    private void classifyLoadedImage(Uri imageUri) {
        // updateResults(null);

        final int orientation = getOrientation(getApplicationContext(), imageUri);
        final ContentResolver contentResolver = this.getContentResolver();

        try {
            final Bitmap croppedFromGallery;
            croppedFromGallery = resizeCropAndRotate(MediaStore.Images.Media.getBitmap(contentResolver, imageUri), orientation);

            runOnUiThread(() -> {
                setImage(croppedFromGallery);
                inferenceTask = new AddData.InferenceTask();
                inferenceTask.execute(croppedFromGallery);
                Toast.makeText(getApplicationContext(), "ESTOY AQUI WE", Toast.LENGTH_LONG).show();
            });
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Unable to load image", Toast.LENGTH_LONG).show();
        }
    }




    protected void setImage(Bitmap image) {
        final int transitionTime = 1000;
        imageSet = true;


        imagenAdd.setImageBitmap(image);
        imagenAdd.setVisibility(View.VISIBLE);


    }

    protected synchronized void initClassifier() {
        if (classifier == null)
            try {
                classifier =
                        com.example.mascotasproject.IA.TensorFlowImageClassifier.create(
                                getAssets(),
                                MODEL_FILE,
                                getResources().getStringArray(R.array.breeds_array),
                                INPUT_SIZE,
                                IMAGE_MEAN,
                                IMAGE_STD,
                                INPUT_NAME,
                                OUTPUT_NAME);
            } catch (OutOfMemoryError | IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), R.string.error_tf_init, Toast.LENGTH_LONG).show();
                });
            }
    }





    // get orientation of picture
    public int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        try (final Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null)
        ) {
            if (cursor.getCount() != 1) {
                cursor.close();
                return -1;
            }

            if (cursor != null && cursor.moveToFirst()) {
                final int r = cursor.getInt(0);
                cursor.close();
                return r;
            }

        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    private Bitmap resizeCropAndRotate(Bitmap originalImage, int orientation) {
        Bitmap result = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Bitmap.Config.ARGB_8888);

        final float originalWidth = originalImage.getWidth();
        final float originalHeight = originalImage.getHeight();

        final Canvas canvas = new Canvas(result);

        final float scale = INPUT_SIZE / originalWidth;

        final float xTranslation = 0.0f;
        final float yTranslation = (INPUT_SIZE - originalHeight * scale) / 2.0f;

        final Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);

        final Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(originalImage, transformation, paint);

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            final Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            result = Bitmap.createBitmap(result, 0, 0, INPUT_SIZE,
                    INPUT_SIZE, matrix, true);
        }

        return result;
    }



    private String getFileExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }








    protected class InferenceTask extends AsyncTask<Bitmap, Void, List<Classifier.Recognition>> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<Classifier.Recognition> doInBackground(Bitmap... bitmaps) {
            initClassifier();

            if (!isCancelled() && classifier != null) {
                return classifier.recognizeImage(bitmaps[0]);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Classifier.Recognition> recognitions) {
            if (!isCancelled())
                updateResults(recognitions);

        }
    }

    void updateResults(List<Classifier.Recognition> results) {
        runOnUiThread(() -> {
            updateResultsView(results);
        });
    }
    // update results on our custom textview
    void updateResultsView(List<Classifier.Recognition> results) {
        String mensaje="";
        final StringBuilder sb = new StringBuilder();
        currentRecognitions = new ArrayList<String>();

        if (results != null) {
            if (results.size() > 0) {
                for (final Classifier.Recognition recog : results) {
                    final String text = String.format(Locale.getDefault(), "%s: %d %%\n",
                            recog.getTitle(), Math.round(recog.getConfidence() * 100));
                    sb.append(text);
                    currentRecognitions.add(recog.getTitle());
                    Log.d("este es el resultado", text);
                }
                mensaje=("Imagen correctamente analizada");
                Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_LONG).show();

            } else {
                //  sb.append(getString(R.string.no_detection));
                imagenAdd.setImageResource(R.mipmap.ic_action_gallery_icon);
                mensaje=("No se reconocio a la mascota, ingresa una mejor foto");
                Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_LONG).show();
            }
        } else {

        }

        final String finalText = sb.toString();
        System.out.println(finalText);

    }


}