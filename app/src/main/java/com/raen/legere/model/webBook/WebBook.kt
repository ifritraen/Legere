package com.raen.legere.model.webBook

import com.raen.legere.data.entities.Book
import com.raen.legere.data.entities.BookChapter
import com.raen.legere.data.entities.BookSource
import com.raen.legere.data.entities.BookSourcePart
import com.raen.legere.data.entities.SearchBook
import com.raen.legere.help.coroutine.Coroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.sync.Semaphore
import kotlin.coroutines.CoroutineContext

typealias WebBookTask<T> = Coroutine<T>

object WebBook {

    suspend fun getBookInfoAwait(bookSource: BookSource?, book: Book, canReName: Boolean = false, isMinInfo: Boolean = false): Book {
        throw UnsupportedOperationException("Offline Only")
    }

    suspend fun getChapterListAwait(bookSource: BookSource?, book: Book, runPreUpdateJs: Boolean = true): Result<List<BookChapter>> {
        return Result.failure(UnsupportedOperationException("Offline Only"))
    }

    suspend fun getContentAwait(bookSource: BookSource?, book: Book, chapter: BookChapter): String {
        throw UnsupportedOperationException("Offline Only")
    }

    suspend fun searchBookAwait(source: BookSource?, searchWord: String): List<SearchBook> {
        return emptyList()
    }

    suspend fun exploreBookAwait(source: BookSource?, url: String): List<SearchBook> {
        return emptyList()
    }

    suspend fun preciseSearchAwait(bookSource: BookSource?, name: String, author: String): Result<SearchBook> {
        return Result.failure(UnsupportedOperationException("Offline Only"))
    }

    fun runPreUpdateJs(source: BookSource?, book: Book) {
        // Offline only
    }

    fun searchBook(scope: CoroutineScope, bookSource: BookSource?, key: String, page: Int): WebBookTask<List<SearchBook>> {
        return Coroutine(scope) { emptyList() }
    }

    fun exploreBook(scope: CoroutineScope, bookSource: BookSource?, url: String, page: Int): WebBookTask<List<SearchBook>> {
        return Coroutine(scope) { emptyList() }
    }

    fun getBookInfo(scope: CoroutineScope, bookSource: BookSource?, book: Book, canReName: Boolean = true): WebBookTask<Book> {
        return Coroutine(scope) { book }
    }

    fun getChapterList(scope: CoroutineScope, bookSource: BookSource?, book: Book, runPreUpdateJs: Boolean = true, isFromBookInfo: Boolean = false): WebBookTask<List<BookChapter>> {
        return Coroutine(scope) { emptyList() }
    }

    fun preciseSearch(
        scope: CoroutineScope,
        bookSourceParts: List<BookSourcePart>,
        name: String,
        author: String,
        semaphore: Semaphore? = null
    ): WebBookTask<Pair<Book, BookSource>> {
        return Coroutine(scope, semaphore = semaphore) { throw UnsupportedOperationException("Offline Only") }
    }

    fun getContent(
        scope: CoroutineScope,
        bookSource: BookSource?,
        book: Book,
        chapter: BookChapter,
        next: Boolean = false,
        context: CoroutineContext = scope.coroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        executeContext: CoroutineContext = scope.coroutineContext,
        semaphore: Semaphore? = null
    ): WebBookTask<String> {
        return Coroutine(scope, context, start, executeContext, semaphore) { "" }
    }
}
