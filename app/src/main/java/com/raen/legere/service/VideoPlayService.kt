package com.raen.legere.service

import android.content.Intent
import com.raen.legere.base.BaseService

class VideoPlayService : BaseService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopSelf()
        return START_NOT_STICKY
    }
}