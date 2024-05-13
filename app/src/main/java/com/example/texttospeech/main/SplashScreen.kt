package com.example.texttospeech.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.texttospeech.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            checkLogin()
        },500)
    }
    private fun checkLogin(){
        val sharedPreference =  getSharedPreferences("user", Context.MODE_PRIVATE)
        val getIdUser = sharedPreference?.getString("id_user","") ?: ""
        if (getIdUser.isNotEmpty()){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}