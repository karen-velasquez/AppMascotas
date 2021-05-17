package com.example.mascotasproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class mapsuser extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapsuser);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String cod=getIntent().getStringExtra("codigo");
        String quien=getIntent().getStringExtra("quien");
        String codMascota=getIntent().getStringExtra("codigoMascota");
        // Add a marker in Sydney and move the camera
        localizacionesMascotas(mMap,cod,codMascota);

    }



    /*-----------LLENANDO LAS UBICACIONES DESDE FIREBASE-------------------------------------*/
    public void localizacionesMascotas(GoogleMap googleMap,String codDueno,String codMascota){
        Log.d("quien entro","pude entrar");
        System.out.print("PUDE ENTRAR FFFFFFFFFFFFFFFFFFFF" );
        LatLng laPaz=new LatLng( -16.5,-68.15);
        mMap=googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
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
                        Log.d("entre latirud",locationMascota.getLatitude()+"longitud"+locationMascota.getLongitud());
                        mascota = new LatLng(Float.parseFloat(locationMascota.getLatitude()), Float.parseFloat(locationMascota.getLongitud()));
                        mMap.addMarker(new MarkerOptions().position(mascota).title(locationMascota.getNombreMas()).snippet("Quizá un resúmen de mascotas perdidas.").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));


                    }


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(laPaz, 10));
    }


}