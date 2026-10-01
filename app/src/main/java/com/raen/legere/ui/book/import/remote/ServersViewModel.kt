package com.raen.legere.ui.book.import.remote

import android.app.Application
import com.raen.legere.base.BaseViewModel
import com.raen.legere.data.appDb
import com.raen.legere.data.entities.Server

class ServersViewModel(application: Application): BaseViewModel(application) {


    fun delete(server: Server) {
        execute {
            appDb.serverDao.delete(server)
        }
    }

}