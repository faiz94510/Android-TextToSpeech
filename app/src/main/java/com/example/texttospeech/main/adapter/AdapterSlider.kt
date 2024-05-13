package com.example.texttospeech.main.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.texttospeech.R


class AdapterSlider(val context: Context, val dataList: Array<String>) : RecyclerView.Adapter<AdapterSlider.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterSlider.MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.fetch_slider, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdapterSlider.MyViewHolder, position: Int) {
        val currentItem = dataList[position]

        Glide.with(context)
            .load(currentItem)
            .into(holder.imageSlider)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(item : View) : RecyclerView.ViewHolder(item){
        val imageSlider : ImageView = item.findViewById(R.id.imageSlider)
    }
}