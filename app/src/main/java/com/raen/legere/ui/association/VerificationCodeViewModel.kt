package com.raen.legere.ui.association

import android.app.Application
import android.os.Bundle
import com.raen.legere.base.BaseViewModel
import com.raen.legere.constant.SourceType
import com.raen.legere.help.source.SourceHelp

class VerificationCodeViewModel(app: Application): BaseViewModel(app) {

    var sourceOrigin = ""
    var sourceName = ""
    private var sourceType = SourceType.book

    fun initData(arguments: Bundle) {
        sourceName = arguments.getString("sourceName") ?: ""
        sourceOrigin = arguments.getString("sourceOrigin") ?: ""
        sourceType = arguments.getInt("sourceType", SourceType.book)
    }

    fun disableSource(block: () -> Unit) {
        block.invoke()
    }

    fun deleteSource(block: () -> Unit) {
        block.invoke()
    }

}
