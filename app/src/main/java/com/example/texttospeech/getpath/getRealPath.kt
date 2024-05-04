package com.example.texttospeech.getpath

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import java.io.File

object getRealPath {
     fun getRealPathFromUri(context: Context, uri: Uri): String? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            FileUtils.getRealPathFromUriLegacy(context, uri)
        } else {
            getRealPathFromUriAboveQ(context, uri)
        }
    }

    // Implementasi getRealPathFromUri() untuk Android di bawah Android 10 (Q)
     object FileUtils {
        fun getRealPathFromUriLegacy(context: Context, uri: Uri): String? {
            var filePath: String? = null
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    filePath = it.getString(columnIndex)
                }
            }
            return filePath
        }
    }

    // Implementasi getRealPathFromUri() untuk Android di atas Android 10 (Q)
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getRealPathFromUriAboveQ(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameColumnIndex: Int = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameColumnIndex != -1) {
                    val fileName: String? = it.getString(displayNameColumnIndex)
                    if (!fileName.isNullOrBlank()) {
                        filePath = context.cacheDir.absolutePath + File.separator + fileName
                    }
                }
            }
        }
        return filePath
    }
}