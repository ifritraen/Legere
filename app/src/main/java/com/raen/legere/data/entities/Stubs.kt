package com.raen.legere.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface BaseSource {
    val getKey: String get() = ""
    val getTag: String get() = ""
    fun getKey(): String = getKey
    fun getTag(): String = getTag
    fun getDisPlayNameGroup(): String = ""
    val enabledCookieJar: Boolean get() = true
    fun getSourceType(): Int = 0
    var concurrentRate: String?
        get() = null
        set(value) {}
    var jsLib: String?
        get() = null
        set(value) {}
    fun getHeaderMap(useCookie: Boolean = true): Map<String, String> = emptyMap()
    fun getVariable(): String = ""
    fun setVariable(value: String?) {}
    fun put(key: String, value: String) {}
    fun get(key: String): String = ""
}

@Parcelize
class BookSource(
    var bookSourceUrl: String = "",
    var bookSourceName: String = "",
    var bookSourceType: String = "0",
    var bookSourceGroup: String? = null,
    var bookSourceComment: String? = null,
    var loginUrl: String? = null,
    var loginUi: String? = null,
    var loginCheckJs: String? = null,
    var coverDecodeJs: String? = null,
    var bookUrlPattern: String? = null,
    var header: String? = null,
    var variableComment: String? = null,
    override var concurrentRate: String? = null,
    override var jsLib: String? = null,
    var enabled: Boolean = true,
    var enabledExplore: Boolean = true,
    override var enabledCookieJar: Boolean = true,
    var eventListener: Boolean = false,
    var customButton: Boolean = false,
    var weight: Int = 0,
    var customOrder: Int = 0,
    var respondTime: Long = 180000L,
    var lastUpdateTime: Long = 0,
    var ruleSearch: SearchRule? = null,
    var ruleBookInfo: BookInfoRule? = null,
    var ruleToc: TocRule? = null,
    var ruleContent: ContentRule? = null,
    var ruleExplore: ExploreRule? = null
) : BaseSource, Parcelable {
    override val getKey: String get() = bookSourceUrl
    override val getTag: String get() = bookSourceName
    fun getDisplayVariableComment(default: String = ""): String = ""
    override fun getDisPlayNameGroup(): String = ""
    fun getSortGroup(): String = ""
    override fun getVariable(): String = ""
    override fun setVariable(value: String?) {}
    override fun getHeaderMap(useCookie: Boolean): Map<String, String> = emptyMap()
    fun putHeader(key: String, value: String) {}
    fun equal(source: BookSource): Boolean = false
}

@Parcelize
class BookSourcePart(
    var bookSourceUrl: String = "",
    var bookSourceName: String = "",
    var bookSourceGroup: String? = null,
    var bookSourceComment: String? = null,
    var enabled: Boolean = true,
    var customOrder: Int = 0
) : Parcelable {
    fun getBookSource(): BookSource? = null
    fun getDisPlayNameGroup(): String = ""
}

@Parcelize
class RssSource(
    var sourceUrl: String = "",
    var sourceName: String = "",
    var sourceIcon: String? = null,
    var sourceGroup: String? = null,
    var sourceComment: String? = null,
    override var concurrentRate: String? = null,
    override var jsLib: String? = null,
    var enabled: Boolean = true,
    var customOrder: Int = 0
) : BaseSource, Parcelable {
    override val getKey: String get() = sourceUrl
    override val getTag: String get() = sourceName
    override fun getVariable(): String = ""
    override fun setVariable(value: String?) {}
    override fun getHeaderMap(useCookie: Boolean): Map<String, String> = emptyMap()
}

@Parcelize
class HttpTTS(
    var id: Long = 0,
    var name: String = "",
    var url: String = "",
    var enabled: Boolean = true
) : Parcelable

@Parcelize
class RssArticle(
    var title: String = "",
    var link: String = "",
    var pubDate: String? = null,
    var description: String? = null,
    var image: String? = null,
    var content: String? = null
) : Parcelable

@Parcelize
class RssReadRecord(
    var link: String = ""
) : Parcelable

@Parcelize
class RssStar(
    var link: String = ""
) : Parcelable

@Parcelize
class RuleSub(
    var sourceUrl: String = ""
) : Parcelable


@Parcelize
class SearchRule(
    var checkKeyWord: String? = null,
    var bookList: String? = null,
    var name: String? = null,
    var author: String? = null,
    var kind: String? = null,
    var wordCount: String? = null,
    var lastChapter: String? = null,
    var intro: String? = null,
    var coverUrl: String? = null,
    var bookUrl: String? = null
) : Parcelable

@Parcelize
class BookInfoRule(
    var init: String? = null,
    var name: String? = null,
    var author: String? = null,
    var kind: String? = null,
    var wordCount: String? = null,
    var lastChapter: String? = null,
    var intro: String? = null,
    var coverUrl: String? = null,
    var tocUrl: String? = null,
    var canReName: String? = null,
    var downloadUrls: String? = null
) : Parcelable

@Parcelize
class TocRule(
    var preUpdateJs: String? = null,
    var chapterList: String? = null,
    var chapterName: String? = null,
    var chapterUrl: String? = null,
    var formatJs: String? = null,
    var isVolume: String? = null,
    var updateTime: String? = null,
    var isVip: String? = null,
    var isPay: String? = null,
    var nextTocUrl: String? = null
) : Parcelable

@Parcelize
class ContentRule(
    var content: String? = null,
    var subContent: String? = null,
    var title: String? = null,
    var nextContentUrl: String? = null,
    var webJs: String? = null,
    var sourceRegex: String? = null,
    var replaceRegex: String? = null,
    var imageStyle: String? = null,
    var imageDecode: String? = null,
    var payAction: String? = null,
    var callBackJs: String? = null
) : Parcelable

@Parcelize
class ExploreRule(
    var bookList: String? = null,
    var name: String? = null,
    var author: String? = null,
    var kind: String? = null,
    var wordCount: String? = null,
    var lastChapter: String? = null,
    var intro: String? = null,
    var coverUrl: String? = null,
    var bookUrl: String? = null
) : Parcelable
