package com.raen.legere.help.webView

import android.webkit.JavascriptInterface
import androidx.appcompat.app.AppCompatActivity
import com.raen.legere.constant.BookType
import com.raen.legere.data.appDb
import com.raen.legere.data.entities.BaseSource
import com.raen.legere.data.entities.Book
import com.raen.legere.data.entities.BookChapter
import com.raen.legere.data.entities.BookSource
import com.raen.legere.help.JsExtensions
import com.raen.legere.model.AudioPlay
import com.raen.legere.model.ReadBook
import com.raen.legere.model.VideoPlay
import com.raen.legere.model.analyzeRule.AnalyzeRule
import com.raen.legere.model.analyzeRule.AnalyzeRule.Companion.setChapter
import java.lang.ref.WeakReference
import java.net.URL

@Suppress("unused")
open class RssJsExtensions(
    activity: AppCompatActivity?,
    source: BaseSource?,
    val bookType: Int = 0
) : JsExtensions {

    val activityRef: WeakReference<AppCompatActivity> = WeakReference(activity)
    val sourceRef: WeakReference<BaseSource?> = WeakReference(source)

    override fun getSource(): BaseSource? {
        return sourceRef.get()
    }

    override fun getTag(): String? {
        return getSource()?.getTag()
    }

    @JavascriptInterface
    fun put(key: String, value: String): String {
        getSource()?.put(key, value)
        return value
    }

    @JavascriptInterface
    fun get(key: String): String {
        return getSource()?.get(key) ?: ""
    }

    @JavascriptInterface
    @JvmOverloads
    fun searchBook(key: String, searchScope: String? = null) {
    }

    fun searchBook(key: String, source: BookSource) {
    }

    @JavascriptInterface
    fun addBook(bookUrl: String) {
    }

    @JavascriptInterface
    fun showPhoto(src: String) {
    }

    @JavascriptInterface
    @JvmOverloads
    fun open(name: String, url: String? = null, title: String? = null, origin: String? = null) {
    }

    /** AnalyzeRule实现 **/
    private val bookAndChapter by lazy {
        var book: Book? = null
        var chapter: BookChapter? = null
        when (bookType) {
            BookType.text -> {
                book = ReadBook.book?.also {
                    chapter = appDb.bookChapterDao.getChapter(
                        it.bookUrl,
                        ReadBook.durChapterIndex
                    )
                }
            }

            BookType.audio -> {
                book = AudioPlay.book
                chapter = AudioPlay.durChapter
            }

            BookType.video -> {
                book = VideoPlay.book
                chapter = VideoPlay.chapter
            }
        }
        Pair(book, chapter)
    }
    private val book: Book? get() = bookAndChapter.first
    private val chapter: BookChapter? get() = bookAndChapter.second

    val analyzeRule by lazy {
        AnalyzeRule(book, source = getSource()).setChapter(chapter)
    }

    @JavascriptInterface
    @JvmOverloads
    fun setContent(content: Any?, baseUrl: String? = null): AnalyzeRule {
        return analyzeRule.setContent(content, baseUrl)
    }

    @JavascriptInterface
    fun setBaseUrl(baseUrl: String?): AnalyzeRule {
        return analyzeRule.setBaseUrl(baseUrl)
    }

    @JavascriptInterface
    fun setRedirectUrl(url: String): URL? {
        return analyzeRule.setRedirectUrl(url)
    }

    @JvmOverloads
    fun getStringList(rule: String?, mContent: Any? = null, isUrl: Boolean = false): List<String>? {
        return analyzeRule.getStringList(rule, mContent, isUrl)
    }

    @JvmOverloads
    fun getString(ruleStr: String?, mContent: Any? = null, isUrl: Boolean = false): String {
        return analyzeRule.getString(ruleStr, mContent, isUrl)
    }

    @JavascriptInterface
    fun getString(ruleStr: String?, unescape: Boolean): String {
        return analyzeRule.getString(ruleStr, unescape)
    }

    fun getElement(ruleStr: String): Any? {
        return analyzeRule.getElement(ruleStr)
    }

    fun getElements(ruleStr: String): List<Any> {
        return analyzeRule.getElements(ruleStr)
    }

}
