package com.example.mascotasproject.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.mascotasproject.MostrarRecycler;
import com.example.mascotasproject.OpcionesUsuario;
import com.example.mascotasproject.R;
import com.example.mascotasproject.SeguimientoMascotas;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.mascotasproject.login.RegisterActivity.getRandomString;

public class ProfileActivity extends AppCompatActivity {

    //Firebase auth
    FirebaseAuth firebaseAuth;


    //views
    TextView mProfileTv, mProfileCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        //init views
        mProfileTv = findViewById(R.id.profileTv);
        mProfileCod= findViewById(R.id.profileCod);

    }

    private void checkUSerStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            Intent intent=new Intent(ProfileActivity.this, OpcionesUsuario.class);
            intent.putExtra("usuario",user.getUid());
            intent.putExtra("quien","Usuario");
            startActivity(intent);
            //user is signed in stay here
            //set email of logged in user
            //mProfileTv.setText(user.getEmail());
           // mProfileCod.setText();

        }else {
            //user is mo signed in, go to main activity
            startActivity(new Intent(ProfileActivity.this, LogInActivity.class));
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart(){
        //check on start of app
        checkUSerStatus();
        super.onStart();
    }

    //inflate options menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  Omflating menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //handle menu item clicks


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        /*if (id== R.id.action_logout){
            firebaseAuth.signOut();
            checkUSerStatus();
        }*/
        return super.onOptionsItemSelected(item);
    }

}