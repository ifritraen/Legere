package com.raen.legere.help.source

import com.raen.legere.data.entities.BookSource
import com.raen.legere.data.entities.SearchBook

val BookSource.exploreUrl: String? get() = null
fun BookSource.exploreUrl(): String? = null
fun BookSource.getBookSource(): BookSource = this
fun BookSource.evalJS(js: String): String = ""
