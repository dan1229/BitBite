package com.example.daniel.digit

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.example.daniel.digit.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}