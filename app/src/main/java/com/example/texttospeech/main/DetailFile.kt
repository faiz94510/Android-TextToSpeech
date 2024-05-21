package com.example.texttospeech.main

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.texttospeech.R
import com.example.texttospeech.databinding.ActivityDetailFileBinding
import com.example.texttospeech.room.database.AppDatabase
import com.example.texttospeech.room.provider.DatabaseProvider
import com.example.texttospeech.statusbar.StatusBarColor
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
    var getId : String = ""
    private lateinit var layoutDialogLogout : View
    private lateinit var alertDialogLogout : AlertDialog.Builder
    private lateinit var dialogLogout : AlertDialog
    companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarColor().InitializationBarColorWithoutStatusBar(this)
        initializationDialog()
        db = DatabaseProvider.getDatabase(this)
        getId = intent.getStringExtra("id")?:""

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
            intent.putExtra("judul", getJudul)
            intent.putExtra("deskripsi", getDeskripsi)
            intent.putExtra("id", getId)
            startActivity(intent)
        }
        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
        binding.menuOVer.setOnClickListener {it->
            showCustomOverflowMenu(it)
        }
    }

    private fun showCustomOverflowMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_overflow, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.btnDelete -> {
                    deleteDataFromDb(getId.toInt())
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    fun deleteDataFromDb(id : Int){
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO){
                db.fileDao().deleteData(id)
            }
            dialogLogout.show()
            Handler().postDelayed({
                dialogLogout.dismiss()
                val intent = Intent(this@DetailFile, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }, 500)

        }
    }
    suspend fun updateLastSeen(id : Int){
        val currentDate = Date(System.currentTimeMillis())
        withContext(Dispatchers.IO) {
            db.fileDao().updateLastSeen(updateAt = currentDate, idFile = id)
        }
    }

    private fun initializationDialog(){
        layoutDialogLogout = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
        alertDialogLogout = AlertDialog.Builder(this).setView(layoutDialogLogout)
        dialogLogout = alertDialogLogout.create()
        dialogLogout.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    private fun intentRead(){
        if (getFilePath.substringAfterLast(".").equals("pdf")){
            val intent = Intent(this, ReadPdf::class.java)
            intent.putExtra("file_path", getFilePath)
            startActivity(intent)
        }else if(getFilePath.substringAfterLast(".").equals("txt")){
            val intent = Intent(this, ReadTxt::class.java)
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
        }else if(getFilePath.substringAfterLast(".").equals("txt")){
            binding.imageCover.setImageResource(R.drawable.notepad_logo)
        }else{
            binding.imageCover.setImageResource(R.drawable.ic_logo)
        }


    }
}