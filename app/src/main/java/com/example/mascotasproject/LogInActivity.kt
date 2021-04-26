package com.example.mascotasproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth

//auth_activity


class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        setup()

    }

    private fun setup(){
        title = "authentication"

        btn_signUp.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.str,
                        passwordEditText.text.str).addOnCompleteListener(){
                    if (it.isSuccessful){
                        showLogedIn(it.result?.user?.email?:"", ProviderType.Basic)
                    }else{
                        showAlert()
                    }

                }
            }
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

    private fun showLogedIn(email: String, provider: ProviderType){

        val logedInIntent = Intent(this, LogedInActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(logedInIntent)
    }

}