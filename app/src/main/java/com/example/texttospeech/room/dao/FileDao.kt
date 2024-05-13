package com.example.texttospeech.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.texttospeech.room.entity.File
import java.sql.Date


@Dao
interface FileDao {
    @Query("SELECT * FROM file")
    fun getAll(): List<File>

    @Query("SELECT * FROM file ORDER BY created_at DESC")
    fun getAllFileByCurrent() : List<File>

    @Query("SELECT * FROM file ORDER BY update_at DESC LIMIT 3")
    fun getLastSeen() : List<File>
    @Insert
    fun insert(file : File)

    @Query("UPDATE file SET update_at = :updateAt WHERE id = :idFile")
    fun updateLastSeen(updateAt : Date, idFile : Int)

}