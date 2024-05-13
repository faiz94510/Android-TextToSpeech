package com.example.texttospeech.room.provider

import android.content.Context
import androidx.room.Room
import com.example.texttospeech.room.database.AppDatabase

object DatabaseProvider {
    private var instance : AppDatabase? = null

    fun getDatabase(context: Context) : AppDatabase{
        return instance ?: synchronized(this){
            val newIntance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "text_tospeech"
            ).fallbackToDestructiveMigration()
                .build()
            instance = newIntance
            newIntance
        }
    }
}