package com.example.texttospeech.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.texttospeech.room.converters.Converters
import com.example.texttospeech.room.dao.FileDao
import com.example.texttospeech.room.entity.File



@Database(entities = [File::class], version = 5)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao
}