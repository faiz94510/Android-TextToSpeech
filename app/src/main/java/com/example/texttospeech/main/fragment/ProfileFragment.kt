package com.example.texttospeech.main.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.texttospeech.R
import com.example.texttospeech.databinding.FragmentProfileBinding
import com.example.texttospeech.main.Login
import com.example.texttospeech.main.MainActivity
import com.example.texttospeech.main.Register
import com.example.texttospeech.room.database.AppDatabase
import com.example.texttospeech.room.provider.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var db: AppDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        db = activity?.let { DatabaseProvider.getDatabase(it) }!!
        getDataProfile()
        getDataFromDb()

        checkLogin()

        binding.btnKeluar.setOnClickListener {
            val intent = Intent(activity, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            activity?.finish()
            val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.clear()
            editor?.apply()
        }

        binding.btnDaftar.setOnClickListener {
            val intent = Intent(activity, Register::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener {
            val intent = Intent(activity, Login::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun checkLogin(){
        val sharedPreference =  activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        val getIdUser = sharedPreference?.getString("id_user","") ?: ""
        if (getIdUser.isNotEmpty()){
            binding.parentAccountAvailable.visibility = View.VISIBLE
            binding.parentLoginRegister.visibility = View.GONE
        }else{
            binding.parentAccountAvailable.visibility = View.GONE
            binding.parentLoginRegister.visibility = View.VISIBLE
        }
    }

    private fun getDataFromDb(){
        CoroutineScope(Dispatchers.Main).launch {
            val getData = withContext(Dispatchers.IO) {
                db.fileDao().getAll()
            }
            // Mengatur teks jumlahTotal di thread UI
            binding.jumlahTotal.text = getData.size.toString()
        }

    }

    private fun getDataProfile(){
        val sharedPreference =  activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        val getNamaLengkap = sharedPreference?.getString("nama_lengkap","") ?: ""
        val getNamaSingkatan = sharedPreference?.getString("nama_singkatan","") ?: ""
        val getEmail = sharedPreference?.getString("email","") ?:""
        binding.email.text = getEmail
        binding.nama.text = getNamaLengkap
        binding.singkatanNama.text = getNamaSingkatan
    }

}