package com.raen.legere.ui.book.read.config

import android.app.Application
import com.raen.legere.base.BaseViewModel

class HttpTtsEditViewModel(application: Application) : BaseViewModel(application) {
    fun initData(id: Long) {}
    fun save(onSuccess: () -> Unit) {
        onSuccess()
    }
}
