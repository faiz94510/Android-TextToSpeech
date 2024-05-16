package com.example.texttospeech.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.texttospeech.R
import com.example.texttospeech.databinding.ActivityReadTxtBinding
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class ReadTxt : AppCompatActivity() {
    private lateinit var binding : ActivityReadTxtBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadTxtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val getPathFile = intent.getStringExtra("file_path")?:""
        if (File(getPathFile).exists()){
            binding.tvContent.text = readTextFromFile( getPathFile)
        }

    }

    private fun readTextFromFile(fileName: String): String {
        return try {
            val file = File(fileName)
            if (file.exists()) {
                return file.readText()
            } else {
                throw IllegalArgumentException("File not found at path: $file")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}