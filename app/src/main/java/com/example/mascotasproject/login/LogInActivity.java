package com.example.mascotasproject.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mascotasproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class LogInActivity extends AppCompatActivity {
    //views
    ImageButton mRegisterBtn, mLogin_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //init views
        mRegisterBtn = (ImageButton)findViewById(R.id.register_btn);
        mLogin_Btn = (ImageButton) findViewById(R.id.login_btn);

        //handle register button click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start REgisterActivity
                startActivity(new Intent(LogInActivity.this, RegisterActivity.class));

            }
        });

        //handle login btn click
        mLogin_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start Login Activity
                startActivity(new Intent(LogInActivity.this, LoggingInActivity.class));
            }
        });


    }
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed(); //go previous activity
        return super.onSupportNavigateUp();
    }

}