package com.raen.legere.data.dao

import com.raen.legere.data.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface BookSourceDao {
    val all: List<BookSource> get() = emptyList()
    val allPart: List<BookSourcePart> get() = emptyList()
    val allEnabled: List<BookSource> get() = emptyList()
    val allEnabledPart: List<BookSourcePart> get() = emptyList()
    val hasBookUrlPattern: List<BookSourcePart> get() = emptyList()
    val maxOrder: Int get() = 0
    val minOrder: Int get() = 0
    val hasDuplicateOrder: Boolean get() = false

    fun getBookSource(url: String): BookSource? = null
    fun getBookSourcePart(url: String): BookSourcePart? = null
    fun getBookSourceAddBook(url: String): BookSource? = null
    fun getEnabledPartByGroup(group: String): List<BookSourcePart> = emptyList()
    fun allGroups(): List<String> = emptyList()
    fun flowEnabledGroups(): Flow<List<String>> = flowOf(emptyList())
    fun flowEnabled(): Flow<List<BookSource>> = flowOf(emptyList())
    fun flowSearchEnabled(): Flow<List<BookSource>> = flowOf(emptyList())
    fun getByKey(url: String): BookSource? = null
    fun has(url: String): Boolean = false

    fun insert(vararg sources: BookSource) {}
    fun update(vararg sources: BookSource) {}
    fun delete(vararg sources: BookSource) {}
    fun delete(url: String) {}
    fun enable(url: String, enable: Boolean) {}
    fun upOrder(sources: List<BookSourcePart>) {}

    companion object {
        val impl = object : BookSourceDao {}
    }
}

interface RssSourceDao {
    val all: List<RssSource> get() = emptyList()
    val allEnabled: List<RssSource> get() = emptyList()
    val maxOrder: Int get() = 0

    fun getSource(url: String): RssSource? = null
    fun insert(vararg sources: RssSource) {}
    fun delete(vararg sources: RssSource) {}
    fun deleteDefault() {}
    fun has(url: String): Boolean = false
    fun getByKey(url: String): RssSource? = null
    fun allGroups(): List<String> = emptyList()

    companion object {
        val impl = object : RssSourceDao {}
    }
}

interface HttpTTSDao {
    val all: List<HttpTTS> get() = emptyList()
    val allEnabled: List<HttpTTS> get() = emptyList()

    fun flowAll(): Flow<List<HttpTTS>> = flowOf(emptyList())
    fun getName(id: Long): String? = null

    fun get(id: Long): HttpTTS? = null
    fun insert(vararg tts: HttpTTS) {}
    fun delete(vararg tts: HttpTTS) {}
    fun deleteDefault() {}

    companion object {
        val impl = object : HttpTTSDao {}
    }
}

interface RssArticleDao {
    companion object {
        val impl = object : RssArticleDao {}
    }
}

interface RssReadRecordDao {
    companion object {
        val impl = object : RssReadRecordDao {}
    }
}

interface RssStarDao {
    val all: List<RssStar> get() = emptyList()
    fun insert(vararg rssStars: RssStar) {}
    companion object {
        val impl = object : RssStarDao {}
    }
}

interface RuleSubDao {
    val all: List<RuleSub> get() = emptyList()
    fun insert(vararg ruleSubs: RuleSub) {}
    companion object {
        val impl = object : RuleSubDao {}
    }
}

interface SearchBookDao {
    fun clearExpired(clearTime: Long) {}
    fun getSearchBook(url: String): SearchBook? = null
    fun insert(vararg searchBooks: SearchBook) {}
    fun getEnableHasCover(name: String, author: String): List<SearchBook> = emptyList()
    fun delete(vararg searchBooks: SearchBook) {}
    fun changeSourceByGroup(group: String, sourceUrl: String) {}
    fun changeSourceSearch(sourceUrl: String, search: Boolean) {}
    fun update(searchBook: SearchBook) {}
    fun getFirstByNameAuthor(name: String, author: String): SearchBook? = null

    companion object {
        val impl = object : SearchBookDao {}
    }
}

interface SearchKeywordDao {
    val all: List<SearchKeyword> get() = emptyList()
    fun insert(vararg searchKeywords: SearchKeyword) {}
    companion object {
        val impl = object : SearchKeywordDao {}
    }
}

