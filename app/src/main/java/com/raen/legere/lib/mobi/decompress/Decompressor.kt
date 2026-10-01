package com.raen.legere.lib.mobi.decompress

interface Decompressor {

    fun decompress(data: ByteArray): ByteArray

}
