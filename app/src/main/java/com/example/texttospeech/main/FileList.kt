package com.example.texttospeech.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.texttospeech.R
import com.example.texttospeech.databinding.ActivityFileListBinding
import com.example.texttospeech.main.adapter.AdapterListFile
import com.example.texttospeech.main.adapter.GridSpacingItemDecoration
import com.example.texttospeech.main.model.DataListFile
import com.example.texttospeech.room.database.AppDatabase
import com.example.texttospeech.room.provider.DatabaseProvider
import com.example.texttospeech.statusbar.StatusBarColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FileList : AppCompatActivity() {
    private lateinit var binding : ActivityFileListBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter : AdapterListFile
    private lateinit var dataList : ArrayList<DataListFile>
    private lateinit var originalDataList: ArrayList<DataListFile>
    private var searchQuery: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarColor().InitializationBarColorWithoutStatusBar(this)
        dataList = ArrayList<DataListFile>()
        originalDataList = ArrayList(dataList)
        adapter = AdapterListFile(this@FileList, dataList)

        db = DatabaseProvider.getDatabase(this)

        fetchFileList()

        binding.edSearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchQuery = p0.toString().trim()
                if (searchQuery.isEmpty()) {
                    dataList.clear()
                    dataList.addAll(originalDataList)
                    adapter.notifyDataSetChanged()
                    adapter.setIsSearching(false)
                } else {
                    // Jika ada teks di EditText, lakukan pencarian
                    adapter.search(searchQuery)
                    adapter.setIsSearching(true)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.backActivity.setOnClickListener {
            onBackPressed()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchFileList(){
        CoroutineScope(Dispatchers.Main).launch {

            withContext(Dispatchers.IO) {
                val getData = db.fileDao().getAllFileByCurrent()
                Log.d("faiz nazhir", getData.toString())


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
                originalDataList.addAll(dataList)

            }

            binding.recyclerView.setHasFixedSize(true)
            val layoutManager = GridLayoutManager(this@FileList, 3)
            val spacingDalamPiksel = resources.getDimensionPixelSize(R.dimen.grid_layout_spacing)
            binding.recyclerView.addItemDecoration(GridSpacingItemDecoration(spacingDalamPiksel, 3))
            binding.recyclerView.layoutManager = layoutManager

            binding.recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

}