package com.example.mascotasproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GetLocation extends AppCompatActivity {

    private ImageView rellenar;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textLatlong;
    private ProgressBar progressBar;
    String lat,longi;
    private Button updatelocation;


    TextView direccion,coordenadas;
    Button donde;

    /*Strings obteniendo los datos del Intent*/
    String mcodDueno,mcodMascota,mnombre,mCaracteristicas,mdatosper,image,quien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        textLatlong = findViewById(R.id.textLatLong);
        progressBar = findViewById(R.id.progressBar);

        rellenar=findViewById(R.id.rellenarimageen);
        updatelocation=findViewById(R.id.subirlocfirebase);

        direccion=findViewById(R.id.mdireccion);
        coordenadas=findViewById(R.id.mcoordenadas);


        /*-------------------------------obteniendo los datos del intent*/
      //  obteniendovaloresIntent();
        /*-------------------------------------------------------------------------------------------------------------*/
        /*-------------------------------subiendo localizaciones-----*/
        updatelocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      subirdatosLocaciones();
            }
        });
        /*-------------------------------------------------------------------------------------------------------------*/



        LocationManager mlocManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion localizacion= new Localizacion();
        localizacion.setGetLocation(this);
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
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        System.out.print("LLEGUE AQUIIIIIIIIIIIIIIII?22222222");
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,(LocationListener)localizacion);




        findViewById(R.id.buttonGetCurrentLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.print("enjghjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
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




    }

    public void setLocation(Location loc){
        //Obtener direccion de la calle a partir de la latitud y longitud
        if(loc.getLatitude()!=0.0 && loc.getLongitude()!=0.0){
            try {
                Geocoder geocoder=new Geocoder(this, Locale.getDefault());
                List<Address> list=geocoder.getFromLocation(
                        loc.getLatitude(),loc.getLongitude(),1);
                System.out.print("LLEGUE AQUIIIIIIIIIIIIIIII?33333333333");
                if(!list.isEmpty()){
                    Address DirCalle=list.get(0);
                    direccion.setText("direccion: "+DirCalle.getAddressLine(0));

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    public void obteniendovaloresIntent(){
        mcodDueno=getIntent().getStringExtra("codDueno");
        mcodMascota=getIntent().getStringExtra("codMascota");
        mnombre=getIntent().getStringExtra("nombreMas");
        mCaracteristicas=getIntent().getStringExtra("caracteristica");
        mdatosper=getIntent().getStringExtra("perdida");
        image=getIntent().getStringExtra("image");
        quien=getIntent().getStringExtra("quien");
    }

    public void subirdatosLocaciones(){
        /*Instanciando el firebase*/

        FirebaseDatabase mFirebaseDatabase;
        DatabaseReference mRef;
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Mascotas/Locaciones");

        /*Subiendo los datos de donde se vio a la mascota*/
        Map<String,Object> locationupdate=new HashMap<>();
        locationupdate.put("latitude",lat);
        locationupdate.put("longitud",longi);
        locationupdate.put("nombreMas",mnombre);
        locationupdate.put("caracteristica",mCaracteristicas);
        locationupdate.put("codigoDueno",mcodDueno);
        locationupdate.put("codigoMascota",mcodMascota);
        mRef.push().setValue(locationupdate);
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



    public class Localizacion implements LocationListener{
        GetLocation getLocation;
        public GetLocation getGetLocation(){return getLocation;}

        public void setGetLocation(GetLocation getLocation){
            this.getLocation=getLocation;
        }


        @Override
        public void onLocationChanged(@NonNull Location location) {
            //Esto se actualiza cada vez que el GPS obtiene nuevas coordenadas

            location.getLatitude();
            location.getLongitude();
            String text="Mi ubicacion actual es: "+"\n Lat="+
                    location.getLatitude()+"\n Long="+location.getLongitude();
            coordenadas.setText(text);
            this.getLocation.setLocation(location);

        }

        @Override
        public void onProviderDisabled(String provider){
            Toast.makeText(GetLocation.this,"GPS DESACTIVADO",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderEnabled(String provider){
            Toast.makeText(GetLocation.this,"GPS ACTIVADO",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){}
    }

}
