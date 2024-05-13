package com.example.texttospeech.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.texttospeech.databinding.ActivityReadEpubBinding



class ReadEpub : AppCompatActivity() {
    private lateinit var binding : ActivityReadEpubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadEpubBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
//    private fun loadEpub() {
//        val folioReader = FolioReader.get()
//        val config = Config()
//        folioReader.setConfig(config, true)
//        val epubFilePath = "/sdcard/Documents/Alices Adventures in Wonderland.epub"
//        folioReader.openBook(epubFilePath)
//    }
}