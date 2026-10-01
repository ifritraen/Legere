package com.raen.legere.ui.book.changesource

import android.os.Bundle
import android.view.View
import com.raen.legere.R
import com.raen.legere.base.BaseDialogFragment
import com.raen.legere.data.entities.Book
import com.raen.legere.data.entities.BookChapter
import com.raen.legere.data.entities.BookSource

class ChangeBookSourceDialog() : BaseDialogFragment(R.layout.dialog_book_change_source) {

    constructor(name: String, author: String) : this() {
        arguments = Bundle().apply {
            putString("name", name)
            putString("author", author)
        }
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        dismiss()
    }

    interface CallBack {
        val oldBook: Book?
        fun changeTo(source: BookSource, book: Book, toc: List<BookChapter>)
    }
}