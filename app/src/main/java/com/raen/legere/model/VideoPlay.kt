package com.raen.legere.model

import com.raen.legere.data.entities.BaseSource
import com.raen.legere.data.entities.Book
import com.raen.legere.data.entities.BookChapter

object VideoPlay {
    var durChapterPos: Int = 0
    var durChapterIndex: Int = 0
    var durChapter: BookChapter? = null
    var longPressSpeed: Int = 10
    var danmakuSpeed: Int = 1
    var danmakuFile: String? = null
    var danmakuStr: String? = null
    var fullBottomProgressBar: Boolean = false
    var episodes: List<Any>? = null
    var lockCurScreen: Boolean = false
    var source: BaseSource? = null
    var book: Book? = null
    var chapter: BookChapter? = null

    val videoManager: VideoManagerStub = VideoManagerStub()

    fun releaseAllVideos() {}
    fun saveRead() {}
    fun upDurIndex(vararg args: Any?) {}

    class VideoManagerStub {
        fun initContext(context: Any) {}
    }
}