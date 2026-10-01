package com.raen.legere.ui.association

import android.app.Application
import android.os.Bundle
import com.raen.legere.base.BaseViewModel
import com.raen.legere.constant.SourceType
import com.raen.legere.data.appDb
import com.raen.legere.help.source.SourceHelp

class OpenUrlConfirmViewModel(app: Application): BaseViewModel(app) {

    var uri = ""
    var mimeType: String? = null
    var sourceOrigin = ""
    var sourceName = ""
    var sourceType = SourceType.book

    fun initData(arguments: Bundle) {
        uri = arguments.getString("uri") ?: ""
        mimeType = arguments.getString("mimeType")
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
