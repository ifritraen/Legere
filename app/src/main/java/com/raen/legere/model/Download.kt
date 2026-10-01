package com.raen.legere.model

import android.content.Context
import com.raen.legere.constant.IntentAction
import com.raen.legere.service.DownloadService
import com.raen.legere.utils.startService

object Download {


    fun start(context: Context, url: String, fileName: String) {
        context.startService<DownloadService> {
            action = IntentAction.start
            putExtra("url", url)
            putExtra("fileName", fileName)
        }
    }

}