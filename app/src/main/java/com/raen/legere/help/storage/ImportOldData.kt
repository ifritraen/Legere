package com.raen.legere.help.storage

import android.content.Context
import android.net.Uri
import com.jayway.jsonpath.DocumentContext
import com.raen.legere.data.entities.BookSource

object ImportOldData {
    fun importUri(context: Context, uri: Uri) {}
    fun importOldSource(json: String): Int = 0
    fun fromOldBookSource(jsonItem: DocumentContext): BookSource = BookSource()
}