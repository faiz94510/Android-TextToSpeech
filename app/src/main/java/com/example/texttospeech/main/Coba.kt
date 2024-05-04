package com.example.texttospeech.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.example.texttospeech.databinding.ActivityCobaBinding
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import java.io.File
import java.net.URLEncoder

import java.util.Locale

class Coba : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding : ActivityCobaBinding
    private lateinit var tts: TextToSpeech
    private var wordIndex = 0
    private lateinit var words: List<String>

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCobaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)
//        binding.webView.loadUrl("file:///android_asset/sample.html")
        // Inisialisasi WebView

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.setSupportZoom(true)
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.displayZoomControls = true
        binding.webView.settings.pluginState = WebSettings.PluginState.ON
        binding.webView.settings.allowFileAccess = true
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.webViewClient = WebViewClient()

        loadPDF()
        // Delay sebelum mengekstrak teks dari WebView
        Handler(Looper.getMainLooper()).postDelayed({
            val pdfText = extractTextFromPDF("/storage/emulated/0/Documents/file coba suara.pdf")
            words = pdfText.split("\\s+".toRegex()) // Pisahkan teks menjadi kata-kata

            // Mulai pembacaan
            speakNextWord()
        }, 3000)
    }
    private fun loadPDF(){
        val pdfPath = "/storage/emulated/0/Documents/file coba suara.pdf" // Ubah dengan path file PDF Anda
        val pdfFile = File(pdfPath)
        // Memeriksa apakah file PDF ada dan dapat diakses
        if (pdfFile.exists()) {
            val encodedPath = "%2Fstorage%2Femulated%2F0%2FDocuments%2Ffile+coba+suara.pdf"
            Log.d("faiz nazhir", encodedPath.toString())
            val pdfUrl = "file:///android_asset/pdfjs/web/viewer.html?file=$encodedPath"
            binding.webView.loadUrl(pdfUrl)
        } else {
            // File tidak ditemukan atau tidak dapat diakses
            // Tindakan yang sesuai dapat dilakukan di sini
            Log.d("faiz nazhir", "bawah")
        }
    }
    @SuppressLint("ObsoleteSdkInt")
    private fun extractTextFromPDF(pdfPath: String): String {
        val reader = PdfReader(pdfPath)
        val pdfDocument = PdfDocument(reader)
        val numPages = pdfDocument.numberOfPages
        val stringBuilder = StringBuilder()

        for (i in 1..numPages) {
            val pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i))
            stringBuilder.append(pageText)
        }

        pdfDocument.close()
        reader.close()

        return stringBuilder.toString()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set bahasa TTS
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Bahasa tidak didukung")
            }
        } else {
            Log.e("TTS", "Inisialisasi gagal")
        }
    }

    private fun speakNextWord() {
        if (wordIndex < words.size) {
            val word = words[wordIndex]
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)

            // Menyoroti kata yang diucapkan
            highlightWord(word)

            wordIndex++

            // Jeda sebelum mengucapkan kata berikutnya (opsional)
            Handler(Looper.getMainLooper()).postDelayed({
                speakNextWord()
            }, 1000)
        }
    }

    private fun highlightWord(word: String) {
        // Implementasi untuk menyoroti kata pada WebView
        binding.webView.evaluateJavascript(
            "javascript:document.body.innerHTML = document.body.innerHTML.replace('${word}', '<span style=\"background-color: yellow;\">${word}</span>');"
        ) { }
    }

    override fun onDestroy() {
        // Hentikan pembacaan TTS saat aplikasi dihentikan
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
        super.onDestroy()
    }
    private fun loadPdfViewerWithPdfPath(pdfPath: String) {
        val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>PDF Viewer</title>
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    overflow: hidden;
                }
                #pdf-container {
                    width: 100%;
                    height: 100vh;
                }
            </style>
            <script src="path/to/pdf.js"></script>
        </head>
        <body>
        <div id="pdf-container"></div>
        <script>
            var pdfPath = '$pdfPath'; // Menggunakan path yang dikirim dari Kotlin

            var loadingTask = pdfjsLib.getDocument(pdfPath);
            loadingTask.promise.then(function(pdf) {
                pdf.getPage(1).then(function(page) {
                    var scale = 1.5;
                    var viewport = page.getViewport({ scale: scale });

                    var canvas = document.createElement('canvas');
                    var context = canvas.getContext('2d');
                    canvas.height = viewport.height;
                    canvas.width = viewport.width;

                    var renderContext = {
                        canvasContext: context,
                        viewport: viewport
                    };
                    page.render(renderContext);
                    document.getElementById('pdf-container').appendChild(canvas);
                });
            });
        </script>
        </body>
        </html>
    """.trimIndent()

        binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

}