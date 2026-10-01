package com.raen.legere.utils

import com.raen.legere.data.entities.BaseSource
import com.raen.legere.data.entities.Book
import java.io.InputStream

/**
 * Image decryption stub tool.
 */
object ImageUtils {

    fun decode(
        src: String, bytes: ByteArray, isCover: Boolean,
        source: BaseSource?, book: Book? = null
    ): ByteArray? {
        return bytes
    }

    fun decode(
        src: String, inputStream: InputStream, isCover: Boolean,
        source: BaseSource?, book: Book? = null
    ): InputStream? {
        return inputStream
    }

    fun skipDecode(source: BaseSource?, isCover: Boolean): Boolean {
        return true
    }
}