package com.example.texttospeech.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.sistempemesananserviskomputer.api.urlAPI
import com.example.texttospeech.R
import com.example.texttospeech.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject

class Login : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnMasuk.setOnClickListener {
            if (binding.edEmail.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }else if (binding.edPassword.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }else{
                binding.btnMasuk.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
                login()
            }
        }

        binding.btnDaftar.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    private fun login(){
        val url = urlAPI.endPoint.url
        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", binding.edEmail.text.toString().trim())
            jsonObject.put("password", binding.edPassword.text.toString().trim())
        }catch ( e: JSONException){
            e.printStackTrace()
        }
        AndroidNetworking.post("$url/auth/login")
            .addJSONObjectBody(jsonObject)
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        Log.d("faiz anazhir", response.toString())
                        if (response.getString("success").equals("true")){
                            binding.btnMasuk.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.INVISIBLE

                            val sharedPreference =  getSharedPreferences("user", Context.MODE_PRIVATE)
                            val editorId = sharedPreference.edit()
                            val getSingkatan = getInitials(response.getString("nama_pengguna"))
                            editorId.putString("id_user", response.getString("id_user"))
                            editorId.putString("nama_singkatan", getSingkatan)
                            editorId.putString("nama_lengkap", response.getString("nama_pengguna"))
                            editorId.putString("email", response.getString("email"))
                            editorId.apply()
                            val intent = Intent(this@Login,MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this@Login, response.getString("message"), Toast.LENGTH_SHORT).show()
                            binding.btnMasuk.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.INVISIBLE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@Login, e.message, Toast.LENGTH_SHORT).show()
                        binding.btnMasuk.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                }
                override fun onError(error: ANError) {
                    val getError = JSONObject(error.errorBody)
                    val message = getError.getString("message")
                    Toast.makeText(this@Login, message, Toast.LENGTH_SHORT).show()
                    binding.btnMasuk.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                }
            })
    }
    fun getInitials(name: String): String {
        val words = name.split(" ")
        val firstWord = words[0].take(1)
        val secondWord = if (words.size > 1) words[1].take(1) else ""
        return "$firstWord$secondWord"
    }
}