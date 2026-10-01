package com.raen.legere.ui.book.changesource

import android.os.Bundle
import android.view.View
import com.raen.legere.R
import com.raen.legere.base.BaseDialogFragment
import com.raen.legere.data.entities.Book
import com.raen.legere.data.entities.BookChapter
import com.raen.legere.data.entities.BookSource

class ChangeChapterSourceDialog() : BaseDialogFragment(R.layout.dialog_chapter_change_source) {

    constructor(name: String, author: String, chapterIndex: Int, chapterTitle: String) : this() {
        arguments = Bundle().apply {
            putString("name", name)
            putString("author", author)
            putInt("chapterIndex", chapterIndex)
            putString("chapterTitle", chapterTitle)
        }
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        dismiss()
    }

    interface CallBack {
        val oldBook: Book?
        fun changeTo(source: BookSource, book: Book, toc: List<BookChapter>)
        fun replaceContent(content: String)
    }
}