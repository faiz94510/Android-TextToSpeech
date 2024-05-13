package com.example.texttospeech.room.converters

import androidx.room.TypeConverter
import java.sql.Date


class Converters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }
}