package com.raen.legere.ui.book.search

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context, key: String? = null) {
            val intent = Intent(context, SearchActivity::class.java)
            context.startActivity(intent)
        }
        fun start(context: Context, source: Any?, key: String? = null) {
            val intent = Intent(context, SearchActivity::class.java)
            context.startActivity(intent)
        }
    }
}
