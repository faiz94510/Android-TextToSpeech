package com.example.texttospeech.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.texttospeech.room.entity.File

@Dao
interface FileDao {
    @Query("SELECT * FROM file")
    fun getAll(): List<File>

    @Query("SELECT * FROM file ORDER BY created_at DESC")
    fun getAllFileByCurrent() : List<File>
    @Insert
    fun insert(file : File)
}