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
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.texttospeech.databinding.ActivityMainBinding
import com.example.texttospeech.getpath.RealPathUtil
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import java.io.File
import java.util.Locale


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener{
    private lateinit var binding : ActivityMainBinding

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var sentences: List<String>
    private var currentSentenceIndex = 0
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
    private fun loadPDFTextFromAssets( file : String): List<String>{
        val textSegments = mutableListOf<String>()
        val reader = PdfReader(file)
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(reader)
        val numPages = pdfDocument.numberOfPages

        for (i in 1..numPages) {
            val pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i))
            val sentencesInPage = pageText.split("[.!?]\\s*".toRegex())
            textSegments.addAll(sentencesInPage)
        }

        pdfDocument.close()
        reader.close()

        return textSegments
    }
    private fun speakNextSentence() {
        if (currentSentenceIndex < sentences.size) {
            highlightCurrentSentence()

            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
            textToSpeech.speak(sentences[currentSentenceIndex], TextToSpeech.QUEUE_FLUSH, params, "UniqueUtteranceId")
        }
    }


    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale("id", "ID")) // Ubah ke bahasa yang diinginkan
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("MainActivity", "Language not supported")
            } else {
                textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        // Move to the next sentence after the current sentence is spoken
                        currentSentenceIndex++
                        if (currentSentenceIndex < sentences.size) {
                            // Speak the next sentence
                            speakNextSentence()
                        }
                    }

                    override fun onError(utteranceId: String?) {}
                })

                // Start TTS process when initialization is done
                speakNextSentence()
            }
        } else {
            Log.e("MainActivity", "TextToSpeech initialization failed")
        }
    }
    private fun highlightCurrentSentence() {
        val sentence = sentences.getOrNull(currentSentenceIndex)
        sentence?.let {
            CoroutineScope(Dispatchers.Main).launch {
                binding.textView.text = sentences.joinToString("\n") { it }
                highlightSentence(currentSentenceIndex)
            }
        }
    }

    private suspend fun highlightSentence(index: Int) {
        val spannableString = SpannableStringBuilder(binding.textView.text)
        val originalColor = binding.textView.currentTextColor
        val highlightedColor = resources.getColor(android.R.color.holo_orange_light, null)

        // Set text color for each sentence
        for (i in sentences.indices) {
            val color = if (i == index) highlightedColor else originalColor
            val start = binding.textView.text.toString().indexOf(sentences[i])
            val end = start + sentences[i].length
            spannableString.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.textView.text = spannableString
        delay(1000)
    }

    override fun onDestroy() {

        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        textToSpeech.shutdown()
        super.onDestroy()
    }
//    private fun highlightCurrentWord() {
//        if (currentWordIndex < words.size) {
//            val start = binding.textView.text.toString().indexOf(words[currentWordIndex])
//            if (start != -1) {
//                val end = start + words[currentWordIndex].length
//                val spannable = SpannableString(binding.textView.text.toString())
//                spannable.setSpan(
//                    BackgroundColorSpan(Color.YELLOW),
//                    start,
//                    end,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//                binding.textView.text = spannable
//            }
//        }
//    }
//    private val ttsListener = object : TextToSpeech.OnUtteranceCompletedListener {
//        override fun onUtteranceCompleted(utteranceId: String?) {
//            runOnUiThread {
//                // Move to the next word after the current word is spoken
//                currentWordIndex++
//                if (currentWordIndex < words.size) {
//                    // Highlight the next word
//                    highlightCurrentWord()
//                    // Speak the next word
//                    speakWord(words[currentWordIndex])
//                }
//            }
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK){
            var uri: Uri? = data?.data
            if (uri != null){
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                    uri = data?.data!!
                    val path = RealPathUtil.getRealPath(this, uri!!)
                    val file = File(path)

                    textToSpeech = TextToSpeech(this, this)
                    // Extract text from PDF using iText
                     sentences = loadPDFTextFromAssets(file.toString())
                    binding.textView.text = sentences.toString()
                    // Split text into words
                    // Clean and separate text
                    // Initialize Text-to-Speech

                    speakNextSentence()
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