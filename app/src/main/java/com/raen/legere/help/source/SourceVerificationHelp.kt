package com.raen.legere.help.source

import com.raen.legere.data.entities.BookSource
import kotlinx.coroutines.CoroutineScope

object SourceVerificationHelp {
    fun verify(scope: CoroutineScope, bookSource: BookSource, callback: (String) -> Unit) {}
}
