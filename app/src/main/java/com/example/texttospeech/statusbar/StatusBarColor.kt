package com.example.texttospeech.statusbar

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.texttospeech.R


class StatusBarColor {
    fun InitializationBarColorUsingStatusBar(activity: Activity) {
        val window = activity.window
        window.statusBarColor = ContextCompat.getColor(activity, R.color.primay)

        // Menghapus flag FLAG_TRANSLUCENT_STATUS
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // Tambahkan flag untuk memastikan tata letak tetap stabil
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun InitializationBarColorWithoutStatusBar(activity: Activity){
        activity.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        activity.actionBar?.hide() // Sembunyikan ActionBar jika digunakan

        // Atur warna background status bar
        activity.window!!.statusBarColor = ContextCompat.getColor(activity, R.color.primay)

        // Pastikan teks pada status bar terlihat dengan baik
        activity.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
    fun InitializationBarColorWithoutStatusBarWhite(activity: Activity){
        activity.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        activity.actionBar?.hide() // Sembunyikan ActionBar jika digunakan

        // Atur warna background status bar
        activity.window!!.statusBarColor = ContextCompat.getColor(activity, R.color.white)

        // Pastikan teks pada status bar terlihat dengan baik
        activity.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}