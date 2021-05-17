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
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.mascotasproject.IA.CameraActivity;
import com.example.mascotasproject.IA.Classifier;
import com.example.mascotasproject.IA.ClassifierActivity;
import com.example.mascotasproject.IA.env.ImageUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity2 extends AppCompatActivity {
    // mobilenet: 224, inception_v3: 299
    private static final int INPUT_SIZE = 299;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128;
    // mobilenet: input, inception_v3: Mul, Keras: input_1
    private static final String INPUT_NAME = "Mul";
    // output, inception(tf): final_result, Keras: output/Softmax
    private static final String OUTPUT_NAME = "final_result";
    private static final String MODEL_FILE = "file:///android_asset/stripped.pb";
    protected MainActivity2.InferenceTask inferenceTask;

    private Classifier classifier;



    protected ArrayList<String> currentRecognitions;
    boolean imageSet = false;


    Button botonimageGallery;
    ImageView imageGallery;


    private static final int PERMISSION_CODE=1001;
    private static final int IMAGE_PICK_CODE=1000;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        botonimageGallery=findViewById(R.id.botonimageGallery);
        imageGallery=findViewById(R.id.imageGallery);


        imageGallery.setOnClickListener(new View.OnClickListener() {
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



    }

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
                imageGallery.setImageURI(data.getData());
                classifyLoadedImage(data.getData());

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
                    inferenceTask = new MainActivity2.InferenceTask();
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

        imageGallery.setImageBitmap(image);
        imageGallery.setVisibility(View.VISIBLE);


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
            } else {
                //  sb.append(getString(R.string.no_detection));
                mensaje=("Saca una mejor foto a la mascota, no se reconocio a la mascota");
            }
        } else {

        }

        final String finalText = sb.toString();
        System.out.println(finalText);

    }



    }

