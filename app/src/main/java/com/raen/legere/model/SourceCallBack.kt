package com.raen.legere.model

import com.raen.legere.data.entities.BaseSource
import com.raen.legere.data.entities.ContentRule
import com.raen.legere.data.entities.BookSource
import kotlinx.coroutines.CoroutineScope

object SourceCallBack {
    const val START_READ = 1
    const val SAVE_READ = 2
    const val ADD_BOOK_SHELF = 3
    const val END_READ = 4
    const val DEL_BOOK_SHELF = 5

    // Click/callback event types
    const val CLICK_CUSTOM_BUTTON = 10
    const val CLICK_SHARE_BOOK = 11
    const val CLICK_COPY_BOOK_URL = 12
    const val CLICK_COPY_TOC_URL = 13
    const val CLICK_CLEAR_CACHE = 14
    const val CLICK_BOOK_LABEL = 15
    const val LONG_CLICK_BOOK_LABEL = 16
    const val CLICK_AUTHOR = 17
    const val LONG_CLICK_AUTHOR = 18
    const val CLICK_BOOK_NAME = 19
    const val LONG_CLICK_BOOK_NAME = 20
    const val LONG_CLICK_CUSTOM_BUTTON = 21

    // Shelf refresh constants
    const val START_SHELF_REFRESH = 30
    const val END_SHELF_REFRESH = 31

    fun getContentRule(source: BaseSource): ContentRule = ContentRule()
    fun callBackBook(vararg args: Any?) {}
    fun callBackBtn(
        activity: Any?,
        action: Int,
        source: Any?,
        book: Any?,
        extra: Any?,
        extra2: Any? = null,
        extra3: Any? = null,
        block: (() -> Unit)? = null
    ) {
        block?.invoke()
    }
    fun callBackSource(scope: CoroutineScope, action: Int, source: Any?) {}
}

fun BookSource.getContentRule(): ContentRule = ContentRule()
