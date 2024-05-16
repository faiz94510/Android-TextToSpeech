package com.example.texttospeech.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.texttospeech.R
import com.example.texttospeech.databinding.ActivityTextToSpeechBinding
import com.example.texttospeech.extracttext.ExtractText
import com.example.texttospeech.statusbar.StatusBarColor
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class TextToSpeech : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding : ActivityTextToSpeechBinding
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var sentences: List<String>
    private var currentSentenceIndex = 0
    var isPause : Boolean = false
    private lateinit var sharedPreferences : SharedPreferences
    var getBahasa : String = ""
    var getWarnaText : Int = 0
    var getWarnaHilight : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextToSpeechBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarColor().InitializationBarColorWithoutStatusBar(this)
        textToSpeech = TextToSpeech(this, this)
        getDatasharedPreferences()

        val getFilePath = intent.getStringExtra("file_path")?:""
        val getExtension = getFilePath.substringAfterLast(".")
        if (getExtension.equals("pdf")){
            sentences = ExtractText.loadPDFTextFromAssets(getFilePath)
            speakNextSentence()
        }else if (getExtension.equals("txt")){
            sentences = ExtractText.loadTxtTextFromFile(getFilePath)
            speakNextSentence()
        }else{
            sentences = ExtractText.loadEpubTextFromFile(getFilePath)
            speakNextSentence()
        }

        binding.btnForward.setOnClickListener {
            currentSentenceIndex++
            speakNextSentence()
        }
        binding.btnRewind.setOnClickListener {
            if (currentSentenceIndex>0){
                currentSentenceIndex--
                speakNextSentence()
            }

        }

        binding.btnPlayPause.setOnClickListener {
            if (isPause){
                isPause = false // Mengubah status isPause menjadi false saat tombol play/pause ditekan
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                binding.textDisplay.text = sentences.toString()
                speakNextSentence()
            } else {
                isPause = true // Mengubah status isPause menjadi true saat tombol play/pause ditekan kembali
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                textToSpeech.stop()
            }
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsTextToSpeech::class.java)
            intent.putExtra("file_path", getFilePath)
            startActivity(intent)
            if (textToSpeech.isSpeaking) {
                textToSpeech.stop()
            }
            textToSpeech.shutdown()
        }

        binding.btnStop.setOnClickListener {
            if (textToSpeech.isSpeaking) {
                textToSpeech.stop()
            }
            textToSpeech.shutdown()
            onBackPressed()
        }
    }

    private fun getDatasharedPreferences(){
        sharedPreferences = getSharedPreferences("text_to_speech", Context.MODE_PRIVATE)
        getBahasa = sharedPreferences.getString("bahasa", "") ?:""
        val defaultWarnaText = ContextCompat.getColor(this@TextToSpeech, R.color.black)
        val defaultWarnaHilight = ContextCompat.getColor(this@TextToSpeech, R.color.hilight_yellow)
        getWarnaHilight = sharedPreferences.getInt("warna_hilight", defaultWarnaHilight)
        getWarnaText = sharedPreferences.getInt("warna_text", defaultWarnaText)



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
            var result: Int = TextToSpeech.ERROR
            if (getBahasa.equals("Inggris")){
                result = textToSpeech.setLanguage(Locale("en"))
            }else if (getBahasa.equals("Indonesia")){
                result = textToSpeech.setLanguage(Locale("id", "ID"))
            }else if (getBahasa.equals("Jawa")){
                result = textToSpeech.setLanguage(Locale("jv", "ID"))
            }else if (getBahasa.equals("Sunda")){
                result = textToSpeech.setLanguage(Locale("su", "ID"))
                Log.d("faiz masuk", "Iya masuk")
            }
             // Ubah ke bahasa yang diinginkan
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
                binding.textDisplay.text = sentences.joinToString("\n") { it }
                highlightSentence(currentSentenceIndex)
            }
        }
    }

    private suspend fun highlightSentence(index: Int) {
        val spannableString = SpannableStringBuilder(binding.textDisplay.text)
        // Set text color for each sentence
        for (i in sentences.indices) {
            val color = if (i == index) getWarnaHilight else Color.TRANSPARENT
            val start = binding.textDisplay.text.toString().indexOf(sentences[i])
            val end = start + sentences[i].length
            spannableString.setSpan(ForegroundColorSpan(getWarnaText), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(BackgroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.textDisplay.text = spannableString
        delay(1000)
    }

    override fun onDestroy() {

        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        textToSpeech.shutdown()
        super.onDestroy()
    }
}