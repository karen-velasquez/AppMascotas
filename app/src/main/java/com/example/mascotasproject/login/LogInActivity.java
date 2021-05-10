package com.example.mascotasproject.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mascotasproject.R;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class LogInActivity extends AppCompatActivity {
    private CallbackManager mCallbackManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //variables para el uso del btn de fb
    private TextView textViewUser;
    private ImageView mLogo;
    private LoginButton fb_btn;
    private AccessTokenTracker accessTokenTracker;
    private static final String TAG = "FacebookAuthentication";

    //views registro
    Button mRegisterBtn, mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //init views
        mRegisterBtn = findViewById(R.id.register_btn);
        mLoginBtn = findViewById(R.id.login_btn);

        //handle register button click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start REgisterActivity
                startActivity(new Intent(LogInActivity.this, RegisterActivity.class));

            }
        });


        mFirebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        textViewUser = findViewById(R.id.text_user);
        mLogo = findViewById(R.id.logo);
        fb_btn = findViewById(R.id.fb_btn);
        fb_btn.setReadPermissions("email,", "public_profile");

        mCallbackManager = CallbackManager.Factory.create();
        fb_btn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError" + error);
            }
        });


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    updateUI(user);
                }else{
                    updateUI(null);
                }
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken != null){
                    mFirebaseAuth.signOut();
                }
            }
        };


    }

    //función para manejar los token entre fb y firebase
    private void handleFacebookToken(AccessToken token){
        Log.d(TAG, "handleFacebookToken" +  token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "sign in with credential: successful");
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    updateUI(user);
                }else{
                    Log.d(TAG, "sign in with credential: failure", task.getException());
                    Toast.makeText(LogInActivity.this, "Autenticación fracasada", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //método para cambiar el estado si es que el log in fue satisfactorio
    private void updateUI(FirebaseUser user){
        if (user != null){
            textViewUser.setText(user.getDisplayName());
            if (user.getPhotoUrl() != null){
                String photoUrl = user.getPhotoUrl().toString();
                photoUrl = photoUrl + "?type=large";
                Picasso.get().load(photoUrl).into(mLogo);
            }
        }else{
            textViewUser.setText("");
            mLogo.setImageResource(R.drawable.com_facebook_button_login_logo);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authStateListener != null){
            mFirebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}