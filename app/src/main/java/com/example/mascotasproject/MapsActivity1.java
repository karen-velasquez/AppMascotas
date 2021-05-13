package com.example.mascotasproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity1 extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker InfoWindow;
    TextView direccion,coordenadas;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    Button donde;


    /*Strings obteniendo los datos del Intent*/
    String mcodDueno,mcodMascota,mnombre,mCaracteristicas,mdatosper,image,quien,codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps1);

        direccion=findViewById(R.id.direccion);
        coordenadas=findViewById(R.id.coordenadas);

        obteniendovaloresIntent();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




/*        if(quien.equals("Usuario")){


        }*/


        LocationManager mlocManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
       // mMap.getUiSettings().setMapToolbarEnabled(true);
        localizacionesMascotas(mMap,mcodDueno,mcodMascota);
         //Antut(mMap);

    }
    public void Antut(GoogleMap googleMap){
        mMap=googleMap;

        final LatLng lapaz = new LatLng(-16.540280161376856, -68.08833198852622);
        mMap.addMarker(new MarkerOptions().position(lapaz).title("Marker in La Paz").snippet("Quizá un resúmen de mascotas perdidas.").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lapaz));
        // Add a marker in La Paz

        LatLng pet1 = new LatLng(-16.522954746025434, -68.08032188119428);
        mMap.addMarker(new MarkerOptions()
                .position(pet1)
                .title("Lost Pet 1").snippet("Quizá un resúmen de mascotas perdidas.")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_weso)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lapaz));


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pet1, 15));
    }



    /*-----------LLENANDO LAS UBICACIONES DESDE FIREBASE-------------------------------------*/
    public void localizacionesMascotas(GoogleMap googleMap,String codDueno,String codMascota){
        System.out.print("PUDE ENTRAR FFFFFFFFFFFFFFFFFFFF" );
        mMap=googleMap;
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Mascotas/Locaciones");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    /*obteniendo los datos de razas desde firebase*/
                    locationMascota locationMascota=ds.getValue(locationMascota.class);
                    LatLng mascota=new LatLng(0.0,0.0);
                    if(locationMascota.getCodigoMascota().equals(codMascota) && locationMascota.getCodigoDueno().equals(codDueno)){
                        //obtener todos los usuarios menos
                        mascota = new LatLng(Float.parseFloat(locationMascota.getLatitude()), Float.parseFloat(locationMascota.getLongitud()));
                        mMap.addMarker(new MarkerOptions().position(mascota).title(locationMascota.getNombreMas()).snippet("Quizá un resúmen de mascotas perdidas.").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                        System.out.print("LLEGUE AQUI PERO NOSE  COMO");

                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mascota, 15));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        if (marker.equals(InfoWindow)){
            InfoPet.newIntance(marker.getTitle(),
                    getString(R.string.PetInfo))
                    .show(getSupportFragmentManager(),null);
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
      //  locationupdate.put("latitude",lat);
     //   locationupdate.put("longitud",longi);
        locationupdate.put("nombreMas",mnombre);
        locationupdate.put("caracteristica",mCaracteristicas);
        locationupdate.put("codigoDueno",mcodDueno);
        locationupdate.put("codigoMascota",mcodMascota);
        mRef.push().setValue(locationupdate);
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

            location.getLatitude();
            location.getLongitude();
            String text="Mi ubicacion actual es: "+"\n Lat="+
                    location.getLatitude()+"\n Long="+location.getLongitude();
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