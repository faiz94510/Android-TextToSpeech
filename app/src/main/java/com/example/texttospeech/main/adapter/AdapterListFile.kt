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
        val imageCover: ImageView = itemView.findViewById(R.id.imageCover)

        fun bind(item: DataListFile) {
            titleFile.text = item.judul

            val file = File(item.path_file)

            if (item.path_file.substringAfterLast(".").equals("pdf")){
                val pdfRendererHelper = PdfRendererHelper(context)
                // Buka file PDF
                val pdfRenderer = pdfRendererHelper.openPdf(file)
                pdfRendererHelper.displayPage(0, imageCover)
                pdfRendererHelper.closePdf()
            }else if(item.path_file.substringAfterLast(".").equals("txt")){
                imageCover.setImageResource(R.drawable.notepad_logo)
            }else{
                imageCover.setImageResource(R.drawable.ic_logo)
            }
            Log.d("kalakaka", item.path_file)



            imageCover.setOnClickListener {
                    val intent = Intent(context, DetailFile::class.java)
                    intent.putExtra("file_path", item.path_file)
                    intent.putExtra("judul", item.judul)
                    intent.putExtra("deskripsi", item.deskripsi)
                    intent.putExtra("id", item.id)
                    context.startActivity(intent)
                }
                parent.setOnClickListener {
                    val intent = Intent(context, DetailFile::class.java)
                    intent.putExtra("file_path", item.path_file)
                    intent.putExtra("judul", item.judul)
                    intent.putExtra("deskripsi", item.deskripsi)
                    intent.putExtra("id", item.id)
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