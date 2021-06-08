package com.example.mascotasproject;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.mascotasproject.IA.Classifier;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EnviarImagenUbicacion extends AppCompatActivity {


    TextView enviardireccion, enviarcoordenadas;
    Button enviarinformacion;
    ImageButton enviarGalleryfoto,enviarCamerafoto;
    ImageView enviarimagenescogida;
    /*Strings obteniendo los datos del Intent*/
    String mcodDueno,mcodMascota,mnombre,mCaracteristicas,mdatosper,image,quien,codigo,mdireccion,mlatitud,mlongitud;


    private static final int PERMISSION_CODE=1001;
    private static final int IMAGE_PICK_CODE=1000;
    private static final int CAMERA_PERM_CODE=100;
    private static final int CAMERA_REQUEST_CODE = 100;

    // mobilenet: 224, inception_v3: 299
    private static final int INPUT_SIZE = 299;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128;
    // mobilenet: input, inception_v3: Mul, Keras: input_1
    private static final String INPUT_NAME = "Mul";
    // output, inception(tf): final_result, Keras: output/Softmax
    private static final String OUTPUT_NAME = "final_result";
    private static final String MODEL_FILE = "file:///android_asset/stripped.pb";
    protected EnviarImagenUbicacion.InferenceTask inferenceTask;
    private Classifier classifier;
    protected ArrayList<String> currentRecognitions;
    boolean imageSet = false;


    String currentPhotoPath;

    //Creando el URI
    Uri mFilePathUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_imagen_ubicacion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        enviardireccion=findViewById(R.id.enviardireccion);
        enviarcoordenadas=findViewById(R.id.enviarcoordenadas);
        enviarinformacion=findViewById(R.id.enviarinformacion);
        enviarGalleryfoto=findViewById(R.id.enviarGalleryFoto);
        enviarCamerafoto=findViewById(R.id.enviarCamaraFoto);
        enviarimagenescogida=findViewById(R.id.enviarimagenescogida);

        mdireccion=getIntent().getStringExtra("direccion");
        mlatitud=getIntent().getStringExtra("latitude");
        mlongitud=getIntent().getStringExtra("longitud");
        enviardireccion.setText(mdireccion);
        enviarcoordenadas.setText("Latitud: "+mlatitud+"\n"+"Longitud: "+mlongitud);


        //AL TOCAR LA IMAGEN
        enviarGalleryfoto.setOnClickListener(new View.OnClickListener() {
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

        enviarCamerafoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSelfPermission(Manifest.permission.CAMERA)==
                        PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(EnviarImagenUbicacion.this,new String[]{Manifest.permission.CAMERA},CAMERA_PERM_CODE);

                }else {
                    dispatchTakePictureIntent();
                }
            }
        });

        enviarinformacion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EnviarImagenUbicacion.this,OpcionesIngreso.class);
                subirdatosLocaciones();
                startActivity(intent);
                finish();

            }
        });

        enviarinformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(enviarimagenescogida.getVisibility() == View.VISIBLE)
                    {
                        subirdatosLocaciones();
                        Intent intent=new Intent(EnviarImagenUbicacion.this, OpcionesIngreso.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(EnviarImagenUbicacion.this,"Debes escoger una imagen que se pueda reconocer",Toast.LENGTH_SHORT).show();
                    }
                }});

    }


    public void subirdatosLocaciones(){
        /*Instanciando el firebase*/
        FirebaseDatabase mFirebaseDatabase;
        DatabaseReference mRef;
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Mascotas/Locaciones");


        mnombre=getIntent().getStringExtra("nombreMas");
        mcodDueno=getIntent().getStringExtra("codDueno");
        mcodMascota=getIntent().getStringExtra("codMascota");
        mCaracteristicas=getIntent().getStringExtra("caracteristica");
        mdatosper=getIntent().getStringExtra("perdida");
        image=getIntent().getStringExtra("image");
        quien=getIntent().getStringExtra("quien");


        /*Subiendo los datos de donde se vio a la mascota*/
        Map<String,Object> locationupdate=new HashMap<>();
        locationupdate.put("latitude",mlatitud);
        locationupdate.put("longitud",mlongitud);
        locationupdate.put("nombreMas",mnombre);
        locationupdate.put("direccion",mdireccion);
        locationupdate.put("codigoDueno",mcodDueno);
        locationupdate.put("codigoMascota",mcodMascota);
        locationupdate.put("fecha",gethora_fecha());
        mRef.push().setValue(locationupdate);
        Toast.makeText(EnviarImagenUbicacion.this,"Ubicacion enviada correctamente",Toast.LENGTH_SHORT).show();
    }
    public String gethora_fecha(){
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        String dateTime=simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("hh:mm:ss a");
        String dateTime2=simpleDateFormat2.format(calendar.getTime());

        return dateTime+" a las "+dateTime2;
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
            case CAMERA_PERM_CODE:
                if (grantResults.length < 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }else{
                    Toast.makeText(this,"Es necesario el permiso para usar la camara",Toast.LENGTH_SHORT).show();
                }

        }
    }


    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
       // File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
            // Create the File where the photo should go
            File photoFile = null;
            Log.d("ESTADO","ENTRE");
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + ".provider",
                        photoFile);
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);}

    }
    private void openCamera() {
        Intent m_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        m_intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(m_intent, CAMERA_REQUEST_CODE);

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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            enviarimagenescogida.setImageURI(data.getData());
            classifyLoadedImage(data.getData());
            mFilePathUri=data.getData();
        }
        if(requestCode==CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            File f = new File(currentPhotoPath);
            enviarimagenescogida.setImageURI(Uri.fromFile(f));
            Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(f);

            classifyLoadedImage(contentUri);
            mFilePathUri=contentUri;
        }




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
                inferenceTask = new EnviarImagenUbicacion.InferenceTask();
                inferenceTask.execute(croppedFromGallery);
            });
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Unable to load image", Toast.LENGTH_LONG).show();
        }
    }




    protected void setImage(Bitmap image) {
        final int transitionTime = 1000;
        imageSet = true;


        enviarimagenescogida.setImageBitmap(image);
        enviarimagenescogida.setVisibility(View.VISIBLE);


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
                enviarimagenescogida.setImageDrawable(null);
                enviarimagenescogida.setVisibility(View.GONE);
                mensaje=("No se reconocio a la mascota, ingresa una mejor foto");
                Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_LONG).show();
            }
        } else {

        }

        final String finalText = sb.toString();
        System.out.println(finalText);

    }

}