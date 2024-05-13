package com.example.texttospeech.main

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.texttospeech.databinding.ActivityAddFileBinding
import com.example.texttospeech.room.database.AppDatabase
import com.example.texttospeech.room.provider.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.sql.Date


class AddFile : AppCompatActivity() {
    private lateinit var binding : ActivityAddFileBinding
    var getPathFile : String = ""
    private lateinit var db: AppDatabase
    companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1
        private const val FILE_PICKER_REQUEST_CODE = 123
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseProvider.getDatabase(this)

        binding.btnUnggah.setOnClickListener {
            checkStoragePermission()
        }

        binding.btnTambahkan.setOnClickListener {
            binding.btnTambahkan.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
               addFile()
            },500)
        }

    }

    private fun addFile(){
        val currentDate =Date(System.currentTimeMillis())
        val fileInsert = com.example.texttospeech.room.entity.File(name_file = binding.edJudul.text.toString().trim(), path_file = getPathFile, deskripsi_file = binding.edDeskripsi.text.toString().trim(), created_at = currentDate, update_at = currentDate)

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                db.fileDao().insert(fileInsert)
            }

            // Setelah operasi insert selesai, menampilkan pesan
            Toast.makeText(this@AddFile, "Data terinput", Toast.LENGTH_SHORT).show()
            binding.btnTambahkan.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddFile.FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK){
            var uri: Uri? = data?.data
            if (uri != null){
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                    uri = data?.data!!
                    val getNameFilePath = getNameFilePathFromUri(this, uri)
                    val copiedFilePath = copyFileFromUri(this, uri)
                    if (copiedFilePath != null) {
                        // File telah berhasil disalin, Anda dapat menggunakan copiedFilePath untuk membuka file
                        Log.d("faiz nazhir aaaa", uri.toString())

//                    val path = getRealPath.getRealPathFromUri(this, uri)
//                        val path = getPathFromUri(this, uri)
//                        val file = File(path)
                        Log.d("faiz nazhir aaaa", copiedFilePath.toString())

                        val getNameFile = getNameFilePath.toString().substringAfterLast("/")
                        getPathFile = copiedFilePath.toString()
                        binding.parentPratinjau.visibility = View.VISIBLE
                        binding.textPdf.text = getNameFile
                        binding.btnUnggah.setText("Ganti berkas")
                    } else {
                        // Terjadi kesalahan saat menyalin file, tangani dengan sesuai
                        Toast.makeText(this, "Gagal menyalin file", Toast.LENGTH_SHORT).show()
                    }


                }
            }
        }

    }
    private fun copyFileFromUri(context: Context, uri: Uri): String? {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            val fileExtension = getFileExtensionFromUri(context, uri)
            val fileName = "file_${System.currentTimeMillis()}.$fileExtension"
            val outputDir = context.cacheDir // Anda bisa menggunakan lokasi penyimpanan yang sesuai dengan kebutuhan Anda
            val outputFile = File(outputDir, fileName)

            inputStream = context.contentResolver.openInputStream(uri)
            outputStream = FileOutputStream(outputFile)

            inputStream?.copyTo(outputStream)

            return outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    private fun getFileExtensionFromUri(context: Context, uri: Uri): String? {
        val contentType = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType)
    }
    private fun getNameFilePathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            filePath = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        }
        cursor?.close()
        return filePath
    }
//    fun getPathFromUri(context: Context, uri: Uri): String? {
//        var filePath: String? = null
//        if (DocumentsContract.isDocumentUri(context, uri)) {
//            when (uri.authority) {
//                "com.android.externalstorage.documents" -> {
//                    val documentId = DocumentsContract.getDocumentId(uri)
//                    val split = documentId.split(":").toTypedArray()
//                    val type = split[0]
//                    if ("primary" == type) {
//                        filePath = "${Environment.getExternalStorageDirectory()}/${split[1]}"
//                    } else {
//                        filePath = "/storage/$type/${split[1]}"
//                    }
//                }
//                "com.android.providers.downloads.documents" -> {
//                    val id = DocumentsContract.getDocumentId(uri)
//                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), id.toLong())
//                    filePath = getDataColumn(context, contentUri, null, null)
//                }
//                "com.android.providers.media.documents" -> {
//                    val documentId = DocumentsContract.getDocumentId(uri)
//                    val split = documentId.split(":").toTypedArray()
//                    val type = split[0]
//                    val contentUri: Uri = when (type) {
//                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//                        else -> MediaStore.Files.getContentUri("external")
//                    }
//                    val selection = "_id=?"
//                    val selectionArgs = arrayOf(split[1])
//                    filePath = getDataColumn(context, contentUri, selection, selectionArgs)
//                }
//            }
//        }
//        return filePath
//    }
//
//    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
//        var filePath: String? = null
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor: Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val columnIndex: Int = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                filePath = it.getString(columnIndex)
//            }
//        }
//        cursor?.close()
//        return filePath
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AddFile.READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFilePicker()
            }
        }
    }
    private fun showFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        val mimeTypes = arrayOf("application/pdf", "application/epub+zip")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, AddFile.FILE_PICKER_REQUEST_CODE)
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Menggunakan SDK_INT >= 33 untuk Android 12
            if (Environment.isExternalStorageManager()) {
                showFilePicker()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, AddFile.READ_EXTERNAL_STORAGE_PERMISSION_CODE)
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
                    AddFile.READ_EXTERNAL_STORAGE_PERMISSION_CODE
                )
            } else {
                showFilePicker()
            }
        }
    }
}