package com.example.texttospeech.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.texttospeech.R
import com.example.texttospeech.main.DetailFile
import com.example.texttospeech.main.PdfRendererHelper
import com.example.texttospeech.main.ReadPdf
import com.example.texttospeech.main.model.DataListFile
import com.github.barteksc.pdfviewer.PDFView
import com.shockwave.pdfium.PdfiumCore
import java.io.File

class AdapterListFile(val context: Context, val dataList : ArrayList<DataListFile>) : RecyclerView.Adapter<AdapterListFile.MyViewHolder>() {
    private var isSearching = false
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterListFile.MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.fetch_book_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdapterListFile.MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val titleFile: TextView = itemView.findViewById(R.id.titleFile)
        val parent: CardView = itemView.findViewById(R.id.parent)
        val imagePdf: ImageView = itemView.findViewById(R.id.imagePdf)

        fun bind(item: DataListFile) {
            titleFile.text = item.judul

            val file = File(item.path_file)
            Log.d("faiz nazhir amrulloh0", item.path_file)
            val pdfRendererHelper = PdfRendererHelper(context)
            // Buka file PDF
            val pdfRenderer = pdfRendererHelper.openPdf(file)


            pdfRendererHelper.displayPage(0, imagePdf)
            pdfRendererHelper.closePdf()



            imagePdf.setOnClickListener {
                    val intent = Intent(context, DetailFile::class.java)
                    intent.putExtra("file_path", item.path_file)
                    intent.putExtra("judul", item.judul)
                    intent.putExtra("deskripsi", item.deskripsi)
                    context.startActivity(intent)
                }
                parent.setOnClickListener {
                    val intent = Intent(context, DetailFile::class.java)
                    intent.putExtra("file_path", item.path_file)
                    intent.putExtra("judul", item.judul)
                    intent.putExtra("deskripsi", item.deskripsi)
                    context.startActivity(intent)
                }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newDataList: List<DataListFile>) {
        dataList.clear()
        dataList.addAll(newDataList)
        notifyDataSetChanged()
    }

    // Fungsi untuk melakukan pencarian berdasarkan kata kunci
    fun search(keyword: String) {
        val filteredList = dataList.filter { it.judul.toLowerCase().contains(keyword.toLowerCase(), ignoreCase = true) }
        setData(filteredList)
    }
    // Fungsi untuk menetapkan status pencarian
    fun setIsSearching(searching: Boolean) {
        isSearching = searching
    }

    // Fungsi untuk mendapatkan status pencarian
    fun isSearching(): Boolean {
        return isSearching
    }
}