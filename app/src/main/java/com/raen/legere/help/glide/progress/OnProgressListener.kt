package com.raen.legere.help.glide.progress

typealias OnProgressListener = (isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long) -> Unit
