package com.example.mascotasproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity1 extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker InfoWindow;
    TextView direccion,coordenadas;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    Button enviarubicacion;
    LocationManager mlocManager;

    double lat;
    double longi;

    /*Strings obteniendo los datos del Intent*/
    String mcodDueno,mcodMascota,mnombre,mCaracteristicas,mdatosper,image,quien,codigo,mdireccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps1);

        direccion=findViewById(R.id.direccion);
        coordenadas=findViewById(R.id.coordenadas);

        enviarubicacion=findViewById(R.id.enviarubicacion);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        enviarubicacion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                subirdatosLocaciones();
                Intent intent=new Intent(MapsActivity1.this,OpcionesIngreso.class);
                startActivity(intent);

            }
        });
/*        if(quien.equals("Usuario")){


        }*/






    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        Log.d("quieb eseeseese",getquien());
        Log.d("quien entro","entre");
            if(getquien().equals("Invitado")){
                mlocManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Localizacion localizacion= new Localizacion();
                localizacion.setMapsActivity1(this);
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MapsActivity1.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                } else {
                    // getCurrentLocation();
                }
                mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,(LocationListener)localizacion);

        }


        //  Antut(mMap);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
       // mMap.getUiSettings().setMapToolbarEnabled(true);


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
        locationupdate.put("latitude",lat+"");
        locationupdate.put("longitud",longi+"");
        locationupdate.put("nombreMas",mnombre);
        locationupdate.put("direccion",mdireccion);
        locationupdate.put("codigoDueno",mcodDueno);
        locationupdate.put("codigoMascota",mcodMascota);
        locationupdate.put("fecha",gethora_fecha());
        mRef.push().setValue(locationupdate);
        Toast.makeText(MapsActivity1.this,"Ubicacion enviada correctamente",Toast.LENGTH_SHORT).show();
    }

    public String gethora_fecha(){
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        String dateTime=simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("hh:mm:ss a");
        String dateTime2=simpleDateFormat2.format(calendar.getTime());

        return dateTime+" a las "+dateTime2;
    }








    public void setLocation(Location loc){
        //Obtener direccion de la calle a partir de la latitud y longitud
        if(loc.getLatitude()!=0.0 && loc.getLongitude()!=0.0){
            try {
                Geocoder geocoder=new Geocoder(this, Locale.getDefault());
                List<Address> list=geocoder.getFromLocation(
                        loc.getLatitude(),loc.getLongitude(),1);
                if(!list.isEmpty()){
                    Address DirCalle=list.get(0);
                    mdireccion=DirCalle.getAddressLine(0)+"";
                    direccion.setText("DIRECCION: "+DirCalle.getAddressLine(0));


                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        if (marker.equals(InfoWindow)){
            InfoPet.newIntance(marker.getTitle(),
                    getString(R.string.PetInfo))
                    .show(getSupportFragmentManager(),null);
        }
    }













    public String getquien(){
        String quien =getIntent().getStringExtra("quien");
        return quien;
    }




    public class Localizacion implements LocationListener{
        MapsActivity1 mapsActivity1;
        public MapsActivity1 getMapsActivity1(){return mapsActivity1;}

        public void setMapsActivity1(MapsActivity1 mapsActivity1){
            this.mapsActivity1=mapsActivity1;
        }


        @Override
        public void onLocationChanged(@NonNull Location location) {
            //Esto se actualiza cada vez que el GPS obtiene nuevas coordenadas


            final LatLng miubicacion = new LatLng(lat, longi);
            mMap.addMarker(new MarkerOptions().position(miubicacion).title("Mi ubicacion").snippet(lat+","+longi).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(miubicacion));
            // Add a marker in La Paz

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miubicacion, 15));


            location.getLatitude();
            location.getLongitude();
            String text="Tu ubicacion actual es: "+"\n Lat="+
                    location.getLatitude()+"\n Long="+location.getLongitude();
            lat=location.getLatitude();
            longi=location.getLongitude();
            coordenadas.setText(text);
            this.mapsActivity1.setLocation(location);

        }

        @Override
        public void onProviderDisabled(String provider){
            Toast.makeText(MapsActivity1.this,"GPS DESACTIVADO",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderEnabled(String provider){
            Toast.makeText(MapsActivity1.this,"GPS ACTIVADO",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){}
    }







}