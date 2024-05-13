package com.example.texttospeech.main


object StringUtils {
    fun getBaseName(filename: String?): String? {
        return removeExtension(getName(filename))
    }

    fun indexOfLastSeparator(filename: String?): Int {
        return if (filename == null) {
            -1
        } else {
            val lastUnixPos = filename.lastIndexOf(47.toChar())
            val lastWindowsPos = filename.lastIndexOf(92.toChar())
            Math.max(lastUnixPos, lastWindowsPos)
        }
    }

    fun getName(filename: String?): String? {
        return if (filename == null) {
            null
        } else {
            val index = indexOfLastSeparator(filename)
            filename.substring(index + 1)
        }
    }

    fun removeExtension(filename: String?): String? {
        return if (filename == null) {
            null
        } else {
            val index = indexOfExtension(filename)
            if (index == -1) filename else filename.substring(0, index)
        }
    }

    fun indexOfExtension(filename: String?): Int {
        return if (filename == null) {
            -1
        } else {
            val extensionPos = filename.lastIndexOf(46.toChar())
            val lastSeparator = indexOfLastSeparator(filename)
            if (lastSeparator > extensionPos) -1 else extensionPos
        }
    }
}