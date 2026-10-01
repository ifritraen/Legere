package com.raen.legere.model.rss

import com.raen.legere.data.entities.RssArticle
import com.raen.legere.data.entities.RssReadRecord
import com.raen.legere.data.entities.RssSource
import com.raen.legere.data.entities.RssStar
import com.raen.legere.model.webBook.WebBookTask
import kotlinx.coroutines.CoroutineScope

fun RssStar.toRssArticle(): RssArticle = RssArticle()
fun RssReadRecord.toRssArticle(): RssArticle = RssArticle()

object Rss {
    fun getContent(
        scope: CoroutineScope,
        rssArticle: RssArticle,
        ruleContent: String,
        rssSource: RssSource
    ): WebBookTask<String> {
        return WebBookTask(scope) { "" }
    }
}
