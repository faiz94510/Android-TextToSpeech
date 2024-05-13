package com.example.texttospeech.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.texttospeech.R
import com.example.texttospeech.databinding.ActivityDetailFileBinding
import java.io.File

class DetailFile : AppCompatActivity() {
    private lateinit var binding : ActivityDetailFileBinding
    var getFilePath : String = ""
    var getJudul : String = ""
    var getDeskripsi : String = ""
    companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()

        binding.btnBuka.setOnClickListener {
           checkStoragePermission()
        }

        binding.btnTextSpeech.setOnClickListener {
            val intent = Intent(this, TextToSpeech::class.java)
            intent.putExtra("file_path", getFilePath)
            startActivity(intent)
        }
    }

    private fun intentRead(){
        val intent = Intent(this, ReadPdf::class.java)
        intent.putExtra("file_path", getFilePath)
        startActivity(intent)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == DetailFile.READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intentRead()
            }
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Menggunakan SDK_INT >= 33 untuk Android 12
            if (Environment.isExternalStorageManager()) {
                intentRead()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, DetailFile.READ_EXTERNAL_STORAGE_PERMISSION_CODE)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    DetailFile.READ_EXTERNAL_STORAGE_PERMISSION_CODE
                )
            } else {
                intentRead()
            }
        }
    }
    private fun loadData(){
        getFilePath = intent.getStringExtra("file_path") ?:""
        getJudul = intent.getStringExtra("judul") ?:""
        getDeskripsi = intent.getStringExtra("deskripsi")?:""

        binding.judul.text = getJudul
        binding.deskripsi.text = getDeskripsi

        binding.pdfView.fromFile(File(getFilePath))
            .defaultPage(0)
            .load()
    }
}