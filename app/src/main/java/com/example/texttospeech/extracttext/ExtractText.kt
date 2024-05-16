package com.example.texttospeech.extracttext

import android.content.Context
import com.github.mertakdut.BookSection
import com.github.mertakdut.Reader
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

object ExtractText {
    fun loadPDFTextFromAssets( file : String): List<String>{
        val textSegments = mutableListOf<String>()
        val reader = PdfReader(file)
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(reader)
        val numPages = pdfDocument.numberOfPages

        for (i in 1..numPages) {
            val pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i))
            val sentencesInPage = pageText.split("[.!?]\\s*".toRegex())
            textSegments.addAll(sentencesInPage)
        }

        pdfDocument.close()
        reader.close()

        return textSegments
    }
    fun loadEpubTextFromFile(filePath: String): List<String> {
        val textSegments = mutableListOf<String>()

        try {
            val reader = Reader()
            reader.setMaxContentPerSection(1000) // Max string length for each page.
            reader.setIsIncludingTextContent(true) // Optional, to return the tags-excluded version.
            reader.setFullContent(filePath) // Must call before readSection.

            var pageIndex = 0
            var hasMorePages = true

            while (hasMorePages) {
                try {
                    val bookSection: BookSection = reader.readSection(pageIndex)
                    val sectionTextContent = bookSection.sectionTextContent // Excludes html tags.

                    if (sectionTextContent.isNotEmpty()) {
                        textSegments.add(sectionTextContent)
                    } else {
                        hasMorePages = false // No more pages available
                    }

                    pageIndex++
                } catch (e: com.github.mertakdut.exception.OutOfPagesException) {
                    // Out of pages, exit loop
                    hasMorePages = false
                }
            }

            // Saving progress is optional
            reader.saveProgress()
            if (reader.isSavedProgressFound) { // Available after calling setFullContent method.
                val lastSavedPage = reader.loadProgress()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return textSegments
    }

    fun loadTxtTextFromFile(filePath: String): List<String> {
        val textSegments = mutableListOf<String>()
        try {
            val file = File(filePath)
            if (file.exists()) {
                val reader = BufferedReader(InputStreamReader(file.inputStream()))
                val stringBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }
                // Remove unwanted symbols using regex
                val cleanedText = stringBuilder.toString().replace("[^A-Za-z0-9.!?\\s]".toRegex(), "")
                // Split the text into segments using regex
                textSegments.addAll(cleanedText.split("[.!?]\\s*".toRegex()))
            } else {
                throw IllegalArgumentException("File not found at path: $filePath")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return textSegments
    }
}