package com.example.texttospeech.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import java.io.File
import java.io.IOException

class PdfRendererHelper(private val context: Context) {
    private var pdfRenderer: PdfRenderer? = null

    fun openPdf(pdfFile: File): PdfRenderer? {
        try {
            val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(fileDescriptor)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return pdfRenderer
    }

    fun closePdf() {
        pdfRenderer?.close()
    }

    fun displayPage(pageIndex: Int, imageView: ImageView) {
        pdfRenderer?.let { renderer ->
            val page = renderer.openPage(pageIndex)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            imageView.setImageBitmap(bitmap)
            page.close()
        }
    }
}