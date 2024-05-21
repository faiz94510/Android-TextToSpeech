package com.example.texttospeech.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import com.example.texttospeech.R
import com.example.texttospeech.databinding.ActivitySettingsTextToSpeechBinding
import com.example.texttospeech.statusbar.StatusBarColor
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener

class SettingsTextToSpeech : AppCompatActivity() {
    private lateinit var binding : ActivitySettingsTextToSpeechBinding
    private lateinit var sharedPreferences : SharedPreferences
    var getBahasa : String = ""
    var getWarnaText : Int = 0
    var getWarnaHilight : Int = 0
    var getId : String = ""
    var getFilePath : String = ""
    var getJudul : String = ""
    var getDeskripsi : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsTextToSpeechBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarColor().InitializationBarColorWithoutStatusBar(this)
        getId = intent.getStringExtra("id")?:""
        getFilePath = intent.getStringExtra("file_path") ?:""
        getJudul = intent.getStringExtra("judul") ?:""
        getDeskripsi = intent.getStringExtra("deskripsi")?:""
        getDatasharedPreferences()


        binding.btnUbahWarnaText.setOnClickListener {
            val dialogColorPicker = AmbilWarnaDialog(this, getWarnaText, object : OnAmbilWarnaListener{
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {

                    getWarnaText = color
                    binding.viewWarnaText.backgroundTintList = ColorStateList.valueOf(getWarnaText)
                    val editor = sharedPreferences.edit()
                    editor.putInt("warna_text", getWarnaText)
                    editor.apply()
                }
            })

            dialogColorPicker.show()

        }
        binding.btnUbahWarnaHilight.setOnClickListener {
            val dialogColorPicker = AmbilWarnaDialog(this, getWarnaHilight, object : OnAmbilWarnaListener{
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {

                    getWarnaHilight = color
                    binding.viewWarnaHilight.backgroundTintList = ColorStateList.valueOf(getWarnaHilight)
                    val editor = sharedPreferences.edit()
                    editor.putInt("warna_hilight", getWarnaHilight)
                    editor.apply()
                }
            })

            dialogColorPicker.show()

        }

        binding.spinnerPilihBahasa.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedItem = p0?.getItemAtPosition(p2).toString()
                val editor = sharedPreferences.edit()
                editor.putString("bahasa", selectedItem.toString())
                editor.apply()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, TextToSpeech::class.java)
        intent.putExtra("file_path", getFilePath)
        intent.putExtra("judul", getJudul)
        intent.putExtra("deskripsi", getDeskripsi)
        intent.putExtra("id", getId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        super.onBackPressed()
    }
    private fun getDatasharedPreferences(){
        sharedPreferences = getSharedPreferences("text_to_speech", Context.MODE_PRIVATE)
        getBahasa = sharedPreferences.getString("bahasa", "") ?:""
        if (getBahasa.equals("Inggris")){
            binding.spinnerPilihBahasa.setSelection(0)
        }else if (getBahasa.equals("Indonesia")){
            binding.spinnerPilihBahasa.setSelection(1)
        }else if (getBahasa.equals("Jawa")){
            binding.spinnerPilihBahasa.setSelection(2)
        }else if (getBahasa.equals("Sunda")){
            binding.spinnerPilihBahasa.setSelection(3)
        }

        val defaultWarnaText = ContextCompat.getColor(this@SettingsTextToSpeech, R.color.black)
        val defaultWarnaHilight = ContextCompat.getColor(this@SettingsTextToSpeech, R.color.hilight_yellow)
        getWarnaHilight = sharedPreferences.getInt("warna_hilight", defaultWarnaHilight)
        getWarnaText = sharedPreferences.getInt("warna_text", defaultWarnaText)




        binding.viewWarnaText.backgroundTintList = ColorStateList.valueOf(getWarnaText)
        binding.viewWarnaHilight.backgroundTintList = ColorStateList.valueOf(getWarnaHilight)

    }
}