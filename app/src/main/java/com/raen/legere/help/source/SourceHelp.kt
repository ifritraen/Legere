package com.raen.legere.help.source

import com.raen.legere.data.entities.BookSource
import com.raen.legere.data.entities.RssSource
import com.raen.legere.data.entities.BaseSource

object SourceHelp {
    fun deleteBookSources(sources: List<BookSource>) {}
    fun deleteRssSources(sources: List<RssSource>) {}
    fun insertBookSources(vararg sources: BookSource) {}
    fun insertRssSources(vararg sources: RssSource) {}
    fun openVideoPlayer(source: BaseSource?, url: String, title: String, isFloat: Boolean) {}
    fun getSource(sourceUrl: String): BaseSource? = null
}
