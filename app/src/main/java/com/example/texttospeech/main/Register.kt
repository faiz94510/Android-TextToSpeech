package com.example.texttospeech.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.sistempemesananserviskomputer.api.urlAPI
import com.example.texttospeech.R
import com.example.texttospeech.databinding.ActivityRegisterBinding
import com.example.texttospeech.statusbar.StatusBarColor
import org.json.JSONException
import org.json.JSONObject

class Register : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarColor().InitializationBarColorWithoutStatusBarWhite(this)

        binding.btnDaftar.setOnClickListener {
            if (binding.edNamaPengguna.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Nama pengguna tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }else if (binding.edEmail.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }else if (binding.edPassword.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }else if (binding.edKonfirmasiPassword.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Konfirmasi password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else if (binding.edPassword.text.toString().trim() != binding.edKonfirmasiPassword.text.toString().trim()){
                Toast.makeText(this, "Konfirmasi password tidak sama", Toast.LENGTH_SHORT).show()
            }else{
                binding.btnDaftar.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
                register()
            }
        }
        binding.btnMasuk.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("intent", "main_activity")
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        super.onBackPressed()
    }

    private fun register(){
        val url = urlAPI.endPoint.url
        val jsonObject = JSONObject()
        try {
            jsonObject.put("nama_pengguna", binding.edNamaPengguna.text.toString().trim())
            jsonObject.put("email", binding.edEmail.text.toString().trim())
            jsonObject.put("password", binding.edPassword.text.toString().trim())
        }catch ( e: JSONException){
            e.printStackTrace()
        }
        AndroidNetworking.post("$url/auth/register")
            .addJSONObjectBody(jsonObject)
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        if (response.getString("success").equals("true")){
                            binding.btnDaftar.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.INVISIBLE

                            val intent = Intent(this@Register,Login::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this@Register, response.getString("message"), Toast.LENGTH_SHORT).show()
                            binding.btnDaftar.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.INVISIBLE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@Register, e.message, Toast.LENGTH_SHORT).show()
                        binding.btnDaftar.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                }
                override fun onError(error: ANError) {
                    val getError = JSONObject(error.errorBody)
                    val message = getError.getString("message")
                    Toast.makeText(this@Register, message, Toast.LENGTH_SHORT).show()
                    binding.btnDaftar.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                }
            })
    }
}