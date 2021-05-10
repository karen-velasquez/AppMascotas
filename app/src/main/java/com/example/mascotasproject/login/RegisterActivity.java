package com.example.mascotasproject.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mascotasproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    //views
    EditText mEmailEt, mPasswordEt;
    Button mRegisterBtn;

    //progressbar to display while registering user
    ProgressDialog progressDialog;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ActionBar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mRegisterBtn = findViewById(R.id.registerBtn);

        //In the onCreate() method, intiliaze the FirebasteAuth instance
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        //handle register btn click
        mRegisterBtn.setOnClickListener((v -> {
            //input email, password
            String email = mEmailEt.getText().toString().trim();
            String password = mPasswordEt.getText().toString().trim();
            //validate
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                //set error and focuss to email editText
                mEmailEt.setError("Invalid Email");
                mEmailEt.setFocusable(true);
            }else if (password.length() < 6){
                //set error and focus to password editText
                mPasswordEt.setError("Password length at least 6 characters");
                mPasswordEt.setFocusable(true);
            }else{
                registerUser(email, password);
            }
        }));


    }



    private void registerUser ( String email, String password){
        //email and password pattern us valid, show progress dialog and start registratering user
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, (task -> {
                    if (task.isSuccessful()){
                        //Sign in succes, dismiss dialog and start register Activity
                        progressDialog.dismiss();
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(RegisterActivity.this, "Registered...\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                        finish();
                    }else{
                        //If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT);
                    }
                })).addOnFailureListener((e -> {
            //error, dismiss progress dialog and get and show the error message
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }));
    }




}