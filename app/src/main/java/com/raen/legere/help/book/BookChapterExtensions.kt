@file:Suppress("unused")

package com.raen.legere.help.book

import com.raen.legere.data.entities.BookChapter
import com.raen.legere.help.RuleBigDataHelp.getDanmakuFile

fun BookChapter.getDanmaku(): Any? { //读取弹幕数据
    return variableMap["danmaku"] ?: getDanmakuFile(bookUrl, url)
}