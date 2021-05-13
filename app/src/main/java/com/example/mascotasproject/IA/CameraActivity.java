package com.example.mascotasproject.IA;

import android.Manifest;
import android.content.ContentResolver;
import android.os.Handler;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mascotasproject.AddData;
import com.example.mascotasproject.GetLocation;
import com.example.mascotasproject.MostrarRecycler;
import com.example.mascotasproject.R;
import com.example.mascotasproject.desfragment;
import com.example.mascotasproject.modeltemporal;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.example.mascotasproject.IA.env.ImageUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;


public abstract class CameraActivity extends FragmentActivity
        implements OnImageAvailableListener, Camera.PreviewCallback {

    static final int PICK_IMAGE = 100;
    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_STORAGE_READ = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String PERMISSION_STORAGE_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    static private final int[] CHART_COLORS = {Color.rgb(114, 147, 203),
            Color.rgb(225, 151, 76), Color.rgb(132, 186, 91), Color.TRANSPARENT};

    public static List<String> supportedLanguageNames;
    public static List<String> supportedLanguageCodes;

    public static String cameraId;
    private static int cameraPermissionRequests = 0;
    protected ArrayList<String> currentRecognitions;
    protected int previewWidth = 0;
    protected int previewHeight = 0;
    protected ClassifierActivity.InferenceTask inferenceTask;
    TextView resultsView;
    PieChart mChart;
    AtomicBoolean snapShot = new AtomicBoolean(false);
    boolean imageSet = false;
    ImageButton cameraButton, closeButton, pick,checkButtonGral,checkButtonuser;
    ToggleButton continuousInferenceButton;
    ImageView imageViewFromGallery;
    ProgressBar progressBar;
    private Handler handler;
    private HandlerThread handlerThread;
    private boolean isProcessingFrame = false;
    private byte[][] yuvBytes = new byte[3][];
    private int[] rgbBytes = null;
    private int yRowStride;
    private Runnable postInferenceCallback;
    private Runnable imageConverter;
    private boolean useCamera2API;
    private String fileUrl;
    private boolean alreadyAdded = false;
    ClassifierActivity classifierActivity;




    String[] resultados=new String[2];

    int IMAGE_REQUEST_CODE=1;

    final String[] urlfoto = {""};

    Uri mFilePathUri;
    //CARPETA DONDE SE ENCONTRARAN LAS IMAGENES
    String mStoragePath = "ImagenesMascotas/";
    StorageReference mStorageReference;

    DatabaseReference myRef;
    public static String preferredLanguageCode;

    abstract void handleSendImage(Intent intent);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        preferredLanguageCode = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("lang", "en");
        supportedLanguageNames = Arrays.asList(getResources().getStringArray(R.array.array_languages));
        supportedLanguageCodes = Arrays.asList(getResources().getStringArray(R.array.array_language_codes));

        setLocale();


        setupButtons();
        setupPieChart();
        initClassifier();

        // Get intent, action and MIME type
        final Intent intent = getIntent();
        final String action = intent.getAction();
        final String type = intent.getType();

        // Handle single image being sent from other application
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                if (inferenceTask != null)
                    inferenceTask.cancel(true);

                handleSendImage(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (permissions[0]) {
                case PERMISSION_CAMERA:
                    setFragment();
                    break;
                case PERMISSION_STORAGE_READ:
                    pickImage();
                    break;
                case PERMISSION_STORAGE_WRITE:
                    // user might have clicked on save or share button
                    //shareButton.callOnClick();
                    break;
            }
        }
    }

    private void setupButtons() {
        imageViewFromGallery = findViewById(R.id.imageView);
        resultsView = findViewById(R.id.results);
        mChart = findViewById(R.id.chart);
        progressBar = findViewById(R.id.progressBar);

        continuousInferenceButton = findViewById(R.id.continuousInferenceButton);
        cameraButton = findViewById(R.id.cameraButton);
     //   shareButton = findViewById(R.id.shareButton);
        checkButtonGral=findViewById(R.id.checkButtoninvitado);
        closeButton = findViewById(R.id.closeButton);
        checkButtonuser=findViewById(R.id.checkButtonusuario);

        pick=findViewById(R.id.pick);
        cameraButton.setEnabled(false);
        //Obteniendo los datos de quien es
        String quien=getIntent().getStringExtra("quien");
        System.out.println("numero 111111"+quien);

        checkButtonGral.setOnClickListener(v-> {
            Intent intent=new Intent(CameraActivity.this, MostrarRecycler.class);
            intent.putExtra("quien",quien);
            intent.putExtra("resultados",resultados);
            startActivity(intent);

        });

        checkButtonuser.setOnClickListener(v-> {
            //verificando que filepathyuri esta vacio o no
            //asignando la instancia de firebasestorage a un objeto de storage
            //Log.d("urlfoto",);
            Intent intent=new Intent(CameraActivity.this, AddData.class);
            intent.putExtra("quien",getQuien());
            intent.putExtra("codigo",getCodigo());
            intent.putExtra("urlimagen",urlfoto[0]);
            intent.putExtra("resultados",resultados);
            startActivity(intent);

        });





        pick.setOnClickListener(v -> {
        if (!hasPermission(PERMISSION_STORAGE_READ)) {
            requestPermission(PERMISSION_STORAGE_READ);
            return ;
        }
        vigenciaafalse(getCodigo());
        pickImage();

        });


        setButtonsVisibility(View.GONE,quien);

            cameraButton.setOnClickListener(v -> {
            if (!hasPermission(PERMISSION_CAMERA)) {
                requestPermission(PERMISSION_CAMERA);
                return;
            }

            vigenciaafalse(getCodigo());
            final View pnlFlash = findViewById(R.id.pnlFlash);

            cameraButton.setEnabled(false);
            snapShot.set(true);
            imageSet = false;
            updateResults(null);

            imageViewFromGallery.setVisibility(View.GONE);
            continuousInferenceButton.setChecked(false);

            // show flash animation
            pnlFlash.setVisibility(View.VISIBLE);
            AlphaAnimation fade = new AlphaAnimation(1, 0);
            fade.setDuration(500);
            fade.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation anim) {
                    pnlFlash.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            pnlFlash.startAnimation(fade);
        });

        resultsView.setOnClickListener(v -> {
            if (currentRecognitions == null|| currentRecognitions.size() == 0)
                return;

            final Intent i = new Intent(getApplicationContext(), com.example.mascotasproject.IA.SimpleListActivity.class);
            i.putStringArrayListExtra("recogs", currentRecognitions);
            startActivity(i);
        });
    }


    /**/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void setLocale() {
        Locale locale;

        if (supportedLanguageCodes.contains(preferredLanguageCode)) {
            locale = new Locale(preferredLanguageCode);
        } else {
            locale = Locale.getDefault();
        }

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());    //restart Activity
    }

    public String getCodigo() {
        String codigo=getIntent().getStringExtra("codigo");
        System.out.println("numero 1quien es el codigo1"+codigo);
        return codigo;
    }
    public String getQuien() {
        String quien=getIntent().getStringExtra("quien");
        System.out.println("numero 1quien es el codigo1"+quien);
        return quien;
    }

    public void vigenciaafalse(String codigo){
        /*Instanciando el firebase*/

        FirebaseDatabase mFirebaseDatabase;
        DatabaseReference mRef;
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("temporalimagen");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    /*obteniendo los datos de razas desde firebase*/
                    modeltemporal modeltemporal = ds.getValue(modeltemporal.class);
                    if(modeltemporal.getCodigo().equals(codigo)){
                        modeltemporal.setVigencia("false");
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:

                final SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.about_message)));
                Linkify.addLinks(s, Linkify.WEB_URLS);

                final AlertDialog dialog = new AlertDialog.Builder(com.example.mascotasproject.IA.CameraActivity.this)
                        .setMessage(s)
                        .setCancelable(true)
                        .setPositiveButton(
                                android.R.string.ok,
                                (d, id) -> d.cancel())
                        .create();

                dialog.show();

                ((TextView) dialog.findViewById(android.R.id.message)).
                        setMovementMethod(LinkMovementMethod.getInstance());

                break;
            case R.id.pick_image:
                if (!hasPermission(PERMISSION_STORAGE_READ)) {
                    requestPermission(PERMISSION_STORAGE_READ);
                    return false;
                }

                pickImage();
                break;
            case R.id.set_language:
                final LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.lang_dialog, null);

                final Spinner langSpinner = dialogView.findViewById(R.id.spinner1);
                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, supportedLanguageNames);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                langSpinner.setAdapter(dataAdapter);
                langSpinner.setSelection(supportedLanguageCodes.indexOf(preferredLanguageCode));

                new AlertDialog.Builder(com.example.mascotasproject.IA.CameraActivity.this)
                        .setTitle(R.string.change_language)
                        .setCancelable(true)
                        .setView(dialogView)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                preferredLanguageCode = supportedLanguageCodes.get(langSpinner.getSelectedItemPosition());
                                PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("lang", preferredLanguageCode).apply();
                                recreate();
                            }
                        })
                        .setIcon(R.drawable.ic_change_language)
                        .show();

                break;
            case R.id.list_breeds:
                startActivity(new Intent(this, com.example.mascotasproject.IA.SimpleListActivity.class));
                break;
            case R.id.action_exit:
                finishAndRemoveTask();
                break;
            default:
                break;
        }

        return true;
    }

    private void pickImage() {
        final Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        continuousInferenceButton.setChecked(false);
        startActivityForResult(i, PICK_IMAGE);
    }


    /*subiendo la imagen al storage a un temporal */
    public void upload_storage(Uri imageUri){

        String mDatabasePath="temporalimagen";
        DatabaseReference mDatabaseReference;

        mDatabaseReference= FirebaseDatabase.getInstance().getReference(mDatabasePath);

        mFilePathUri=imageUri;
        mStorageReference= FirebaseStorage.getInstance().getReference();
        if(mFilePathUri!=null) {
            StorageReference storageReference2nd = mStorageReference.child(mStoragePath + System.currentTimeMillis() + "." +"jpg");
            //adicionando un suceso a storagereference2nd
            storageReference2nd.putFile(mFilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //sacando la url de storage
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uri.isComplete()) ;
                            Uri url = uri.getResult();
                            System.out.println("EL URL DONDE SE GUARDO ES "+url);

                            Toast.makeText(CameraActivity.this,"Cargando succeso",Toast.LENGTH_SHORT).show();

                            urlfoto[0] =url+"";
                            modeltemporal modelo=new modeltemporal(getCodigo(),url.toString(),"true");
                            //obteniendo el id de la imagen subida
                            String imageUploadId= mDatabaseReference.push().getKey();
                            //adicionando la imagen cargada a los id's de los elementos hijos dentro databasereference
                            mDatabaseReference.child(imageUploadId).setValue(modelo);



                        }
                    });

            //    eliminardatosredundantestemporal(getCodigo(),urlfoto+"");
        }
    }

    protected int[] getRgbBytes() {
        if (imageConverter != null)
            imageConverter.run();

        return rgbBytes;
    }

    /**
     * Callback for android.hardware.Camera API
     */
    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera) {
        if (isProcessingFrame) {
            return;
        }

        try {
            // Initialize the storage bitmaps once when the resolution is known.
            if (rgbBytes == null) {
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                previewHeight = previewSize.height;
                previewWidth = previewSize.width;
                rgbBytes = new int[previewWidth * previewHeight];
                onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
            }
        } catch (final Exception ignored) {
            return;
        }

        isProcessingFrame = true;
        yuvBytes[0] = bytes;
        yRowStride = previewWidth;

        imageConverter =
                () -> ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);

        postInferenceCallback =
                () -> {
                    camera.addCallbackBuffer(bytes);
                    isProcessingFrame = false;
                };
        processImage();
    }

    /**
     * Callback for Camera2 API
     */
    @Override
    public void onImageAvailable(final ImageReader reader) {
        //We need to wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return;
        }
        if (rgbBytes == null) {
            rgbBytes = new int[previewWidth * previewHeight];
        }
        try {
            final Image image = reader.acquireLatestImage();

            if (image == null) {
                return;
            }

            if (isProcessingFrame) {
                image.close();
                return;
            }
            isProcessingFrame = true;
            final Plane[] planes = image.getPlanes();
            fillBytes(planes, yuvBytes);
            yRowStride = planes[0].getRowStride();
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();

            imageConverter = () -> ImageUtils.convertYUV420ToARGB8888(
                    yuvBytes[0],
                    yuvBytes[1],
                    yuvBytes[2],
                    previewWidth,
                    previewHeight,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    rgbBytes);

            postInferenceCallback = () -> {
                image.close();
                isProcessingFrame = false;
            };

            processImage();
        } catch (final Exception ignored) {
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        if (!hasPermission(PERMISSION_CAMERA)) {

            if (cameraPermissionRequests++ < 3) {
                requestPermission(PERMISSION_CAMERA);
            } else {
                Toast.makeText(getApplicationContext(), "Camera permission required.", Toast.LENGTH_LONG).show();
            }
        } else {
            setFragment();
        }

        snapShot.set(false);

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        final ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

        if (!imageSet) cameraButton.setEnabled(true);
    }

    private void setupPieChart() {
        mChart.getDescription().setEnabled(false);
        mChart.setUsePercentValues(true);
        mChart.setTouchEnabled(false);

        // show center text only first time
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final boolean previouslyStarted = prefs.getBoolean("showhelp", false);
        if (!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("showhelp", Boolean.TRUE);
            edit.apply();

            mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf"));
            mChart.setCenterText(generateCenterSpannableText());
            mChart.setCenterTextSizePixels(23);
            mChart.setDrawCenterText(true);
        }

        mChart.setExtraOffsets(14, 0.f, 14, 0.f);
        mChart.setHoleRadius(85);
        mChart.setHoleColor(Color.TRANSPARENT);
        mChart.setHovered(true);
        mChart.setDrawMarkers(false);
        mChart.setRotationEnabled(false);
        mChart.setHighlightPerTapEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setAlpha(0.9f);

        // display unknown slice
        final ArrayList<PieEntry> entries = new ArrayList<>();
        // set unknown slice to transparent
        entries.add(new PieEntry(100, ""));
        final PieDataSet set = new PieDataSet(entries, "");
        set.setColor(R.color.transparent);
        set.setDrawValues(false);

        final PieData data = new PieData(set);
        mChart.setData(data);
    }

    private SpannableString generateCenterSpannableText() {
        final SpannableString s = new SpannableString("Center dog here\nkeep camera stable");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 15, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 15, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, 15, 0);

        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 18, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 18, s.length(), 0);
        return s;
    }

    @Override
    public synchronized void onPause() {
        snapShot.set(false);
        cameraButton.setEnabled(false);
        isProcessingFrame = false;
        progressBar.setVisibility(View.GONE);

        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException ignored) {
        }

        super.onPause();
    }

    @Override
    public synchronized void onStop() {
        super.onStop();
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }

    private boolean hasPermission(final String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission(final String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{permission}, PERMISSIONS_REQUEST);
        }
    }

    // Returns true if the device supports the required hardware level, or better.
    private boolean isHardwareLevelSupported(
            CameraCharacteristics characteristics, int requiredLevel) {
        int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            return requiredLevel == deviceLevel;
        }
        // deviceLevel is not LEGACY, can use numerical sort
        return requiredLevel <= deviceLevel;
    }

    protected String chooseCamera() {
        final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (final String cameraId : manager.getCameraIdList()) {
                final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                final StreamConfigurationMap map =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                if (map == null) {
                    continue;
                }

                // Fallback to camera1 API for internal cameras that don't have full support.
                // This should help with legacy situations where using the camera2 API causes
                // distorted or otherwise broken previews.
                useCamera2API = (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                        || isHardwareLevelSupported(characteristics,
                        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
                return cameraId;
            }
        } catch (CameraAccessException ignored) {
        }

        return null;
    }

    protected void setFragment() {
        cameraId = chooseCamera();
        if (cameraId == null) {
            Toast.makeText(getApplicationContext(), "No Camera Detected", Toast.LENGTH_SHORT).show();
            finish();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, useCamera2API ? new CameraConnectionFragment() : new LegacyCameraConnectionFragment())
                .commitAllowingStateLoss();
    }

    protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (int i = 0; i < planes.length; ++i) {
            final ByteBuffer buffer = planes[i].getBuffer();
            if (yuvBytes[i] == null) {
                //LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
                yuvBytes[i] = new byte[buffer.capacity()];
            }
            buffer.get(yuvBytes[i]);
        }
    }

    protected void readyForNextImage() {
        // sometimes this will be uninitialized, for whatever reason
        if (postInferenceCallback != null) {
            postInferenceCallback.run();
        } else isProcessingFrame = false;

    }

    protected int getScreenOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    protected abstract void processImage();

    protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

    protected abstract void initClassifier();

    void updateResults(List<Classifier.Recognition> results) {
        runOnUiThread(() -> {
            updateResultsView(results);
        });
    }

    void setButtonsVisibility(final int visibility, String quien) {
        final boolean enabled = visibility == View.VISIBLE;
        System.out.println("QUE ENTROOOOOO ADENTRO DE LA VISIBILIDAD"+quien);
        if(quien.toString().equals("Invitado")){
            checkButtonGral.setVisibility(visibility);
            checkButtonGral.setEnabled(enabled);
        }
        if(quien.equals("Usuario")){
            checkButtonuser.setVisibility(visibility);
            checkButtonuser.setEnabled(enabled);
        }
        closeButton.setVisibility(visibility);
        closeButton.setEnabled(enabled);
      // saveButton.setVisibility(visibility);
       // saveButton.setEnabled(enabled);
    }

    // update results on our custom textview
    void updateResultsView(List<Classifier.Recognition> results) {
        String mensaje="";
        int cont=0;
        final StringBuilder sb = new StringBuilder();
        currentRecognitions = new ArrayList<String>();

        if (results != null) {
            resultsView.setEnabled(true);

            //Obteniendo los datos de quien es
            String quien=getIntent().getStringExtra("quien");
            System.out.println("numero 111111"+quien);
            setButtonsVisibility(View.VISIBLE,quien);


            if (results.size() > 0) {
                for (final Classifier.Recognition recog : results) {
                    final String text = String.format(Locale.getDefault(), "%s: %d %%\n",
                            recog.getTitle(), Math.round(recog.getConfidence() * 100));
                    sb.append(text);
                    currentRecognitions.add(recog.getTitle());
                    resultados[cont]=recog.getTitle();
                    cont++;
                }
                mensaje=("Imagen correctamente analizada");
            } else {
              //  sb.append(getString(R.string.no_detection));
                mensaje=("Saca una mejor foto a la mascota, no se reconocio a la mascota");
            }
        } else {
            resultsView.setEnabled(false);
        }

        final String finalText = sb.toString();
        //resultsView.setText(finalText);
        resultsView.setText(mensaje);
        System.out.println(finalText);

    }


    protected void setImage(Bitmap image) {
        final int transitionTime = 1000;
        imageSet = true;
        alreadyAdded = false;

        cameraButton.setEnabled(false);
        imageViewFromGallery.setImageBitmap(image);
        imageViewFromGallery.setVisibility(View.VISIBLE);

        final TransitionDrawable transition = (TransitionDrawable) imageViewFromGallery.getBackground();
        transition.startTransition(transitionTime);


        // fade out image on click
        final AlphaAnimation fade = new AlphaAnimation(1, 0);
        fade.setDuration(transitionTime);

        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (inferenceTask != null)
                    inferenceTask.cancel(true);

                imageViewFromGallery.setClickable(false);
                runInBackground(() -> updateResults(null));
                transition.reverseTransition(transitionTime);
                imageViewFromGallery.setVisibility(View.GONE);
                //Obteniendo los datos de quien es
                String quien=getIntent().getStringExtra("quien");
                System.out.println("numero 111111"+quien);
                setButtonsVisibility(View.GONE, quien);
            }

            @Override
            public void onAnimationEnd(Animation anim) {
                progressBar.setVisibility(View.GONE);
                imageSet = false;
                snapShot.set(false);
                cameraButton.setEnabled(true);
                readyForNextImage();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageViewFromGallery.setVisibility(View.VISIBLE);
        closeButton.setOnClickListener(v -> imageViewFromGallery.startAnimation(fade));


    }

    public Bitmap takeScreenshot() {
        //Obteniendo los datos de quien es
        String quien=getIntent().getStringExtra("quien");
        System.out.println("numero 111111"+quien);
        setButtonsVisibility(View.GONE,quien);
        final View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        final Bitmap b = rootView.getDrawingCache();
        setButtonsVisibility(View.VISIBLE,quien);
        return b;
    }





    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }




}