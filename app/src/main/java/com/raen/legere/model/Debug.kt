package com.raen.legere.model

import android.annotation.SuppressLint
import android.util.Log
import com.raen.legere.BuildConfig
import com.raen.legere.data.entities.*
import kotlinx.coroutines.CoroutineScope

object Debug {
    var callback: Callback? = null
    val debugMessageMap = HashMap<String, String>()
    var isChecking: Boolean = false

    fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d("sourceDebug", msg)
        }
        callback?.printLog(1, msg)
    }

    fun log(
        sourceUrl: String?,
        msg: String = "",
        print: Boolean = true,
        isHtml: Boolean = false,
        showTime: Boolean = true,
        state: Int = 1
    ) {
        log(msg)
    }

    fun startDebug(scope: CoroutineScope, bookSource: BookSource, book: Book) {}
    fun startDebug(scope: CoroutineScope, bookSource: BookSource, key: String) {}
    fun startDebug(scope: CoroutineScope, rssSource: RssSource) {}
    fun startDebug(scope: CoroutineScope, bookSource: BookSource, chapter: BookChapter) {}
    fun cancelDebug() {}

    interface Callback {
        fun printLog(state: Int, msg: String)
    }
}