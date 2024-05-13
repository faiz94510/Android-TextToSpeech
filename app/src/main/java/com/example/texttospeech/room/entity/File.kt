package com.example.texttospeech.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date


@Entity
data class File(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name_file: String,
    val path_file: String,
    val deskripsi_file: String,
    val created_at: Date,
    val update_at: Date?
)
