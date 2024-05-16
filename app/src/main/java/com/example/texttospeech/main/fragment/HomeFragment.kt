package com.example.texttospeech.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.texttospeech.R
import com.example.texttospeech.databinding.FragmentHomeBinding
import com.example.texttospeech.main.AddFile
import com.example.texttospeech.main.FileList
import com.example.texttospeech.main.adapter.AdapterListFile
import com.example.texttospeech.main.adapter.AdapterSlider
import com.example.texttospeech.main.adapter.GridSpacingItemDecoration
import com.example.texttospeech.main.model.DataListFile
import com.example.texttospeech.room.database.AppDatabase
import com.example.texttospeech.room.provider.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private var currentPage = 0
    private var timer: Timer? = null
    private lateinit var db: AppDatabase
    private lateinit var adapter : AdapterListFile
    private lateinit var dataList : ArrayList<DataListFile>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        dataList = ArrayList<DataListFile>()
        adapter = activity?.let { AdapterListFile(it, dataList) }!!
        db = activity?.let { DatabaseProvider.getDatabase(it) }!!
        getDataProfile()
        imageSlider()
        fetchFileList()

        binding.tanggalHariIni.text = getTodayDate()

        binding.daftarBook.setOnClickListener {
            val intent = Intent(activity, FileList::class.java)
            startActivity(intent)
        }
        binding.tambahkanBook.setOnClickListener {
            val intent = Intent(activity, AddFile::class.java)
            startActivity(intent)
        }



        return binding.root

    }
    private fun getDataProfile(){
        val sharedPreference =  activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        val getNamaLengkap = sharedPreference?.getString("nama_lengkap","") ?: ""
        if (getNamaLengkap.isEmpty()){
            binding.namaPengguna.text = "Halo, selamat datang"
        }else{
            binding.namaPengguna.text = getNamaLengkap
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun fetchFileList(){
        CoroutineScope(Dispatchers.Main).launch {

            withContext(Dispatchers.IO) {
                val getData = db.fileDao().getLastSeen()
                if (getData.isEmpty()){
                    binding.terakhirDilihat.visibility = View.INVISIBLE
                    binding.terakhirDilihat.visibility = View.INVISIBLE
                }else{
                    binding.terakhirDilihat.visibility = View.VISIBLE
                    binding.terakhirDilihat.visibility = View.VISIBLE
                }
                for (file in getData) {
                    val adaptedData = DataListFile(
                        id = file.id.toString(),
                        judul = file.name_file,
                        path_file = file.path_file,
                        deskripsi = file.deskripsi_file
                    )
                    // Menambahkan data yang telah diadaptasi ke dalam list
                    dataList.add(adaptedData)
                }
            }

            binding.recyclerView.setHasFixedSize(true)
            val layoutManager = GridLayoutManager(activity, 3)
            val spacingDalamPiksel = resources.getDimensionPixelSize(R.dimen.grid_layout_spacing)
            binding.recyclerView.addItemDecoration(GridSpacingItemDecoration(spacingDalamPiksel, 3))
            binding.recyclerView.layoutManager = layoutManager
            binding.recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
    private fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val todayDate = Date()
        return dateFormat.format(todayDate)
    }
    private fun imageSlider(){

        val urlImage = arrayOf("https://www.denpasarkota.go.id/public/uploads/berita/berita_192709090957_InidiaAlamatdanLokasiGramediadiDenpasar,BagiKamuYangGemarMembacaBuku.jpg",
            "https://blog.mayar.id/content/images/size/w2000/2023/09/woman-holding-tablet-touch-screen-1.jpg",
            "https://www.simplilearn.com/ice9/free_resources_article_thumb/what_is_image_Processing.jpg")
        val adapter = activity?.let { AdapterSlider(it, urlImage) }
        binding.viewPager.adapter = adapter
        binding.dotsIndicator.setViewPager2(binding.viewPager)
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    if (currentPage == urlImage.size) {
                        currentPage = 0
                    }
                    binding.viewPager.setCurrentItem(currentPage++, true)
                }
            }
        }, 5000, 5000)


    }
}