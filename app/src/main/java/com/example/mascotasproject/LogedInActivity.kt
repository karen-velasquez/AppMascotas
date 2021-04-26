package com.example.mascotasproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

enum class ProviderType {
    Basic
}

//HomeActivity
class LogedInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loged_in)
    }
}