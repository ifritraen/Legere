package com.raen.legere.model.remote

import android.net.Uri

class RemoteBookWebDav(val rootBookUrl: String, mAuthorization: Any?, val serverID: Long = 0) {
    fun getRemoteBookList(url: String): List<RemoteBook> = emptyList()
    fun downloadRemoteBook(remoteBook: RemoteBook): Uri = Uri.EMPTY
    fun getRemoteBook(url: String): RemoteBook? = null
    fun upload(book: Any?) {}
}

class RemoteBook {
    var isDir: Boolean = false
    var isOnBookShelf: Boolean = false
    var contentType: String = ""
    var size: Long = 0
    var lastModify: Long = 0
    var filename: String = ""
    var path: String = ""
}
