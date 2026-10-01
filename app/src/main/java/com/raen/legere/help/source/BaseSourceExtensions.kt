package com.raen.legere.help.source

import com.raen.legere.constant.SourceType
import com.raen.legere.data.entities.BaseSource
import com.raen.legere.data.entities.BookSource
import com.raen.legere.data.entities.RssSource
import com.raen.legere.model.SharedJsScope
import org.mozilla.javascript.Scriptable
import kotlin.coroutines.CoroutineContext

fun BaseSource.getShareScope(coroutineContext: CoroutineContext? = null): Scriptable? {
    return SharedJsScope.getScope(jsLib, coroutineContext)
}

fun BaseSource.getSourceType(): Int {
    return when (this) {
        is BookSource -> SourceType.book
        is RssSource -> SourceType.rss
        else -> error("unknown source type: ${this::class.simpleName}.")
    }
}
