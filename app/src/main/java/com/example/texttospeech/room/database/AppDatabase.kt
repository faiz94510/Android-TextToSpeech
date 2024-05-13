package com.example.texttospeech.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.texttospeech.room.converters.Converters
import com.example.texttospeech.room.dao.FileDao
import com.example.texttospeech.room.dao.UserDao
import com.example.texttospeech.room.entity.File
import com.example.texttospeech.room.entity.User


@Database(entities = [User::class, File::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun fileDao(): FileDao
}