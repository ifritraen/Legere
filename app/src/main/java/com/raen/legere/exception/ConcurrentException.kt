@file:Suppress("unused")

package com.raen.legere.exception

/**
 * 并发限制
 */
class ConcurrentException(msg: String, val waitTime: Long) : NoStackTraceException(msg)