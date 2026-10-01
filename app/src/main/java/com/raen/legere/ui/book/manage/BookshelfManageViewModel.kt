package com.raen.legere.ui.book.manage

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.raen.legere.R
import com.raen.legere.base.BaseViewModel
import com.raen.legere.constant.AppLog
import com.raen.legere.constant.BookType
import com.raen.legere.data.appDb
import com.raen.legere.data.entities.Book
import com.raen.legere.data.entities.BookSource
import com.raen.legere.help.book.BookHelp
import com.raen.legere.help.book.isLocal
import com.raen.legere.help.book.removeType
import com.raen.legere.help.config.AppConfig
import com.raen.legere.help.coroutine.Coroutine
import com.raen.legere.model.localBook.LocalBook
import com.raen.legere.model.webBook.WebBook
import com.raen.legere.model.SourceCallBack
import com.raen.legere.utils.FileUtils
import com.raen.legere.utils.GSON
import com.raen.legere.utils.stackTraceStr
import com.raen.legere.utils.toastOnUi
import com.raen.legere.utils.writeToOutputStream
import kotlinx.coroutines.delay
import java.io.File


class BookshelfManageViewModel(application: Application) : BaseViewModel(application) {
    var groupId: Long = -1L
    var groupName: String? = null
    val batchChangeSourceState = MutableLiveData<Boolean>()
    val batchChangeSourceProcessLiveData = MutableLiveData<String>()
    var batchChangeSourceCoroutine: Coroutine<Unit>? = null

    fun upCanUpdate(books: List<Book>, canUpdate: Boolean) {
        execute {
            val array = Array(books.size) {
                books[it].copy(canUpdate = canUpdate).apply {
                    if (!canUpdate) {
                        removeType(BookType.updateError)
                    }
                }
            }
            appDb.bookDao.update(*array)
        }
    }

    fun updateBook(vararg book: Book) {
        execute {
            appDb.bookDao.update(*book)
        }
    }

    fun deleteBook(books: List<Book>, deleteOriginal: Boolean = false) {
        execute {
            appDb.bookDao.delete(*books.toTypedArray())
            books.forEach {
                if (it.isLocal) {
                    LocalBook.deleteBook(it, deleteOriginal)
                } else {
                    val source = appDb.bookSourceDao.getBookSource(it.origin)
                    SourceCallBack.callBackBook(SourceCallBack.DEL_BOOK_SHELF, source, it)
                }
            }
        }
    }

    fun saveAllUseBookSourceToFile(success: (file: File) -> Unit) {
    }

    fun changeSource() {}

    fun clearCache(books: List<Book>) {
        execute {
            books.forEach {
                BookHelp.clearCache(it)
            }
        }.onSuccess {
            context.toastOnUi(R.string.clear_cache_success)
        }
    }

}