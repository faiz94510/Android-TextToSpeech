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
import com.example.texttospeech.databinding.ActivityReadPdfBinding
import java.io.File

class ReadPdf : AppCompatActivity() {
    private lateinit var binding: ActivityReadPdfBinding

    var getPathFile : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPathFile = intent.getStringExtra("file_path")?:""
        displayFromFile(File(getPathFile))


    }

    private fun displayFromFile(file: File) {
        binding.pdfView.fromFile(file)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .enableAnnotationRendering(false)
            .password(null)
            .scrollHandle(null)
            .load()
    }
}