package com.example.mascotasproject

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.mascotasproject.IA.ClassifierActivity
import com.example.mascotasproject.IA.LogInActivity
import com.google.android.gms.location.FusedLocationProviderClient


class MainActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))



        //Botón Get Location
        val btnsegundo: Button =findViewById(R.id.btn_segundo)
        btnsegundo.setOnClickListener{
            val intent= Intent(this, OpcionesIngreso:: class.java)
            startActivity(intent)
        }

        val btnAdd: Button =findViewById(R.id.btn_add)
        btnAdd.setOnClickListener{
            val intent= Intent(this, AddData:: class.java)
            startActivity(intent)
        }

        val btnia: Button =findViewById(R.id.ia)
        btnia.setOnClickListener{
            val intent= Intent(this, ClassifierActivity:: class.java)
            startActivity(intent)
        }

        val btcard: Button =findViewById(R.id.btn_cardview)
        btcard.setOnClickListener{
            val intent= Intent(this, MostrarRecycler:: class.java)
            startActivity(intent)
        }

        //botón log In
        val btn_logIn = findViewById<Button>(R.id.btn_logIn)

        btn_logIn.setOnClickListener{
            val intent= Intent(this, LogInActivity:: class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //acciones al hacer click en el action bar

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

     fun fetchLocation(){
        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if(it != null){
                Toast.makeText(applicationContext, "${it.latitude} ${it.longitude}", Toast.LENGTH_SHORT).show()
            }
        }

    }

}