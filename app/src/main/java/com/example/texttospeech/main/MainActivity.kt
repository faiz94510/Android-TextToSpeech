package com.example.texttospeech.main


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.texttospeech.R
import com.example.texttospeech.databinding.ActivityMainBinding
import com.example.texttospeech.main.fragment.HomeFragment
import com.example.texttospeech.main.fragment.ProfileFragment
import com.example.texttospeech.statusbar.StatusBarColor


class MainActivity : AppCompatActivity(){
    private lateinit var binding : ActivityMainBinding
    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable { doubleBackToExitPressedOnce = false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarColor().InitializationBarColorWithoutStatusBar(this)
        val getIntent = intent.getStringExtra("intent")?:""
        if (getIntent.equals("main_activity")){
            replaceFragment(ProfileFragment())
            binding.bottomNav.selectedItemId = R.id.profileFragment
        }else{
            replaceFragment(HomeFragment())
            binding.bottomNav.selectedItemId = R.id.homeFragment
        }
        binding.bottomNav.setOnItemSelectedListener{
            when(it.itemId){
                R.id.homeFragment -> replaceFragment(HomeFragment())
                R.id.profileFragment -> replaceFragment(ProfileFragment())
                else->{}
            }
            true
        }
        binding.btnTambahBuku.setOnClickListener {
            val intent = Intent(this, AddFile::class.java)
            startActivity(intent)
        }
    }
    private fun AppCompatActivity.replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onBackPressed() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.fragmentContainer)

        if (fragment is HomeFragment || fragment is ProfileFragment) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()

            handler.postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)
        } else {
            // If not on the HomeFragment, perform the normal back press action
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

}