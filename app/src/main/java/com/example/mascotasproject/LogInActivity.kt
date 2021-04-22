package com.example.mascotasproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.credentials.IdentityProviders.FACEBOOK
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

//auth_activity


class LogInActivity : AppCompatActivity() {

    private val Google_SING_IN = 100

    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        setup()
        session()

    }

    override fun onStart() {
        super.onStart()

        authLayout.visibility = View.visible
    }

    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null){
            authLayout.visibility = View.invisible
            showLoggedIn(email, ProviderType.valueOf(provider))
        }
    }

    private fun setup(){
        title = "Authentication"
        btn_signUp.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.str,
                        passwordEditText.text.str).addOnCompleteListener(){
                    if (it.isSuccessful){
                        showLoggedIn(it.result?.user?.email?:"", ProviderType.Basic)
                    }else{
                        showAlert()
                    }

                }
            }
        }

        btn_logIn.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){

                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.str,
                        passwordEditText.text.str).addOnCompleteListener(){
                    if (it.isSuccessful){
                        showLoggedIn(it.result?.user?.email?:"", ProviderType.Basic)
                    }else{
                        showAlert()
                    }

                }
            }
        }

        btn_google.setOnClickListener {

            //Config

            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail().
                    build()

            val googleClient = GoogleSignInOptions.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.singInIntent,Google_SING_IN)
        }

        btn_fb.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult>{

                    override fun onSuccess(result: LoginResult?){

                        result?.let {

                            val token = it.accessToken

                             val credential = FacebookAuthProvider.getCredential(token.token)

                              FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{

                                 if (it.isSuccessful){
                                     showLoggedIn(it.result?.email ?: "", ProviderType.FACEBOOK)
                                 }else{
                                    showAlert()
                                 }
                              }
                         }
                     }

                    override fun onCancel(result: LoginResult?){

                       }

                    override fun onError(result: LoginResult?){
                        showAlert()
                     }
                })
        }
    }

    private fun showAlert(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Sea ha producido un error en la autenticación del usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showLoggedIn(email: String, provider: ProviderType){

        val loggedInIntent = Intent(this, LoggedInActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(loggedInIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Google_SING_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                val account = task.getResult(ApiException::class.java)

                if (account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{

                        if (it.isSuccessful){
                         showLoggedIn(account.email ?: "", ProviderType.GOOGLE)
                         }else{
                             showAlert()
                        }
                    }
                }
            } catch (e: ApiException){
                showAlert()
            }


        }
    }

}