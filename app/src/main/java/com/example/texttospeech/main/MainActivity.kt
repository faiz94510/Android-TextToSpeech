package com.example.texttospeech.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment

import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.texttospeech.databinding.ActivityMainBinding
import com.example.texttospeech.getpath.RealPathUtil
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor

import java.io.File
import java.util.Locale


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener{
    private lateinit var binding : ActivityMainBinding

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var words: List<String>
    private var currentWordIndex = 0
    companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1
        private const val FILE_PICKER_REQUEST_CODE = 123
    }
    var getText : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Process text (remove special characters, split into words, etc.)



        // Start TTS process

        binding.btn.setOnClickListener {

        }
        binding.picker.setOnClickListener {

            checkStoragePermission()
        }

    }
    private fun loadPDFTextFromAssets( file : String): String {
        val reader = PdfReader(file)
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(reader)
        val numPages = pdfDocument.numberOfPages
        val stringBuilder = StringBuilder()

        for (i in 1..numPages) {
            val pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i))
            // Membersihkan teks
            val cleanText = cleanText(pageText)
            // Memisahkan teks menjadi kalimat atau paragraf
            val separatedText = separateText(cleanText)
            stringBuilder.append(separatedText)
        }

        pdfDocument.close()
        reader.close()

        return stringBuilder.toString()
    }
    private fun cleanText(text: String): String {
        // Hilangkan karakter khusus seperti tanda baca yang tidak diinginkan
        var cleanedText = text.replace(Regex("[^A-Za-z0-9.,!?\\s]"), "")
        // Hilangkan format yang tidak diinginkan
        cleanedText = cleanedText.replace(Regex("\\s+"), " ")
        return cleanedText
    }

    private fun separateText(text: String): String {
        // Pisahkan teks menjadi kalimat menggunakan tanda baca "." dan "?"
        val sentences = text.split(Regex("(?<=[.!?])\\s+"))
        // Gabungkan kembali kalimat yang dipisahkan dengan tanda baca
        val separatedText = sentences.joinToString("\n")
        return separatedText
    }

    private fun startTextToSpeech() {
        if (currentWordIndex < words.size) {
            highlightCurrentWord()
            // Speak the next word
            speakWord(words[currentWordIndex])
        }
    }

    private fun speakWord(word: String) {
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
        textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, params, "UniqueUtteranceId")
        textToSpeech.setOnUtteranceCompletedListener(ttsListener)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language not supported
            } else {
                // Start TTS process when initialization is done
                startTextToSpeech()
            }
        } else {
            // TTS initialization failed
        }
    }

    override fun onDestroy() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        textToSpeech.shutdown()
        super.onDestroy()
    }
    private fun highlightCurrentWord() {
        if (currentWordIndex < words.size) {
            val start = binding.textView.text.toString().indexOf(words[currentWordIndex])
            if (start != -1) {
                val end = start + words[currentWordIndex].length
                val spannable = SpannableString(binding.textView.text.toString())
                spannable.setSpan(
                    BackgroundColorSpan(Color.YELLOW),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.textView.text = spannable
            }
        }
    }
    private val ttsListener = object : TextToSpeech.OnUtteranceCompletedListener {
        override fun onUtteranceCompleted(utteranceId: String?) {
            runOnUiThread {
                // Move to the next word after the current word is spoken
                currentWordIndex++
                if (currentWordIndex < words.size) {
                    // Highlight the next word
                    highlightCurrentWord()
                    // Speak the next word
                    speakWord(words[currentWordIndex])
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK){
            var uri: Uri? = data?.data
            if (uri != null){
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                    uri = data?.data!!
                    val path = RealPathUtil.getRealPath(this, uri!!)
                    val file = File(path)

                    // Extract text from PDF using iText
                    val text = loadPDFTextFromAssets(file.toString())
                    binding.textView.text = text
                    // Split text into words
                    words = text.split("\\s+".toRegex())

                    // Initialize Text-to-Speech
                    textToSpeech = TextToSpeech(this, this)
                    startTextToSpeech()
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainActivity.READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFilePicker()
            }
        }
    }
    private fun showFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf" // Atur tipe MIME sesuai kebutuhan
        startActivityForResult(intent, MainActivity.FILE_PICKER_REQUEST_CODE)
    }
    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Menggunakan SDK_INT >= 33 untuk Android 12
            if (Environment.isExternalStorageManager()) {
                showFilePicker()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, MainActivity.READ_EXTERNAL_STORAGE_PERMISSION_CODE)
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
                    MainActivity.READ_EXTERNAL_STORAGE_PERMISSION_CODE
                )
            } else {
                showFilePicker()
            }
        }
    }
}