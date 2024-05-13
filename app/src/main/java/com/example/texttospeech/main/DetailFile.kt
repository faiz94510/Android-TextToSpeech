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
import com.example.texttospeech.room.database.AppDatabase
import com.example.texttospeech.room.provider.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.sql.Date

class DetailFile : AppCompatActivity() {
    private lateinit var binding : ActivityDetailFileBinding
    var getFilePath : String = ""
    var getJudul : String = ""
    var getDeskripsi : String = ""
    private lateinit var db: AppDatabase
    companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseProvider.getDatabase(this)
        val getId = intent.getStringExtra("id")?:""

        CoroutineScope(Dispatchers.Main).launch {
            updateLastSeen(getId.toInt())
        }


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
    suspend fun updateLastSeen(id : Int){
        val currentDate = Date(System.currentTimeMillis())
        withContext(Dispatchers.IO) {
            db.fileDao().updateLastSeen(updateAt = currentDate, idFile = id)
        }
    }
    private fun intentRead(){
        if (getFilePath.substringAfterLast(".").equals("pdf")){
            val intent = Intent(this, ReadPdf::class.java)
            intent.putExtra("file_path", getFilePath)
            startActivity(intent)
        }else{
            val intent = Intent(this, ReadEpub::class.java)
            intent.putExtra("file_path", getFilePath)
            startActivity(intent)
        }

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

        if (getFilePath.substringAfterLast(".").equals("pdf")){
            val pdfRendererHelper = PdfRendererHelper(this)
            // Buka file PDF
            val pdfRenderer = pdfRendererHelper.openPdf(File(getFilePath))
            pdfRendererHelper.displayPage(0, binding.imageCover)
            pdfRendererHelper.closePdf()
        }else{
            binding.imageCover.setImageResource(R.drawable.ic_logo)
        }


    }
}