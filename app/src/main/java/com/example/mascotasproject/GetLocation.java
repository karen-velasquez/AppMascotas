package com.example.mascotasproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GetLocation extends AppCompatActivity {

    private ImageView rellenar;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textLatlong;
    private ProgressBar progressBar;
    String lat,longi;
    private Button updatelocation;


    /*Subiendo los datos de donde se vio a la mascota*/
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        textLatlong = findViewById(R.id.textLatLong);
        progressBar = findViewById(R.id.progressBar);

        rellenar=findViewById(R.id.rellenarimageen);
        updatelocation=findViewById(R.id.subirlocfirebase);


        /*-------------------------------obteniendo los datos del intent*/
        String mnombre=getIntent().getStringExtra("nombreMas");
        String mCaracteristicas=getIntent().getStringExtra("caracteristica");
        String mdatosper=getIntent().getStringExtra("perdida");
        String image=getIntent().getStringExtra("image");
        String quien=getIntent().getStringExtra("quien");
        /*-------------------------------------------------------------------------------------------------------------*/


        Bundle bundle=getIntent().getExtras();

        Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("imagen");
        rellenar.setImageBitmap(bitmap);


        findViewById(R.id.buttonGetCurrentLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            GetLocation.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                } else {
                    getCurrentLocation();
                }
            }
        });

        /*Instanciando el firebase*/
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Mascotas/Locaciones");

        updatelocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> locationupdate=new HashMap<>();
                locationupdate.put("latitude",lat);
                locationupdate.put("longitud",longi);
                locationupdate.put("nombreMas",mnombre);
                locationupdate.put("caracteristica",mCaracteristicas);
                mRef.push().setValue(locationupdate);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {

        progressBar.setVisibility(View.VISIBLE);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(GetLocation.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(GetLocation.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;

                            double latitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            lat=latitude+"";
                            double longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            longi=longi+"";


                            textLatlong.setText(
                                    String.format(
                                            "Latitude: %s\nLongitude: %s",
                                            latitude,
                                            longitude
                                    )
                            );
                        }
                    }
                }, Looper.getMainLooper());
    }
}
