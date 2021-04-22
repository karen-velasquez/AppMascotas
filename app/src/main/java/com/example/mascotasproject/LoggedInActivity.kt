package com.example.mascotasproject

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.credentials.IdentityProviders.FACEBOOK
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    Basic,
    GOOGLE
}

//HomeActivity
class LoggedInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.`activity_logged_in`)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")

        //Guardado de datos

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("prvider", provider)
        prefs.apply()
    }
}

    private fun setup(email: String, provider: String){
        title = "Inicio"
        emailTextView.text = email
        providerTextView.text = provider

        btn_logOut.setOnClickListener{

            //Borrado de datos

            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            if(provider == ProviderType.FACEBOOK.name){
                LoginManager.getInstance().logOut()
            }

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
