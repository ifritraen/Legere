package com.raen.legere.ui.association

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.raen.legere.base.BaseViewModel
import com.raen.legere.constant.AppConst
import com.raen.legere.constant.AppLog
import com.raen.legere.constant.AppPattern
import com.raen.legere.data.appDb
import com.raen.legere.data.entities.ReplaceRule
import com.raen.legere.exception.NoStackTraceException
import com.raen.legere.help.ReplaceAnalyzer
import com.raen.legere.help.http.decompressed
import com.raen.legere.help.http.newCallResponseBody
import com.raen.legere.help.http.okHttpClient
import com.raen.legere.help.http.text
import com.raen.legere.utils.isAbsUrl
import com.raen.legere.utils.isJsonArray
import com.raen.legere.utils.isJsonObject
import com.raen.legere.utils.isUri
import com.raen.legere.utils.readText
import com.raen.legere.utils.splitNotBlank
import splitties.init.appCtx

class ImportReplaceRuleViewModel(app: Application) : BaseViewModel(app) {
    var isAddGroup = false
    var groupName: String? = null
    val errorLiveData = MutableLiveData<String>()
    val successLiveData = MutableLiveData<Int>()

    val allRules = arrayListOf<ReplaceRule>()
    val checkRules = arrayListOf<ReplaceRule?>()
    val selectStatus = arrayListOf<Boolean>()

    val isSelectAll: Boolean
        get() {
            selectStatus.forEach {
                if (!it) {
                    return false
                }
            }
            return true
        }

    val selectCount: Int
        get() {
            var count = 0
            selectStatus.forEach {
                if (it) {
                    count++
                }
            }
            return count
        }

    fun importSelect(finally: () -> Unit) {
        execute {
            val group = groupName?.trim()
            val selectRules = arrayListOf<ReplaceRule>()
            selectStatus.forEachIndexed { index, b ->
                if (b) {
                    val rule = allRules[index]
                    if (!group.isNullOrEmpty()) {
                        if (isAddGroup) {
                            val groups = linkedSetOf<String>()
                            rule.group?.splitNotBlank(AppPattern.splitGroupRegex)?.let {
                                groups.addAll(it)
                            }
                            groups.add(group)
                            rule.group = groups.joinToString(",")
                        } else {
                            rule.group = group
                        }
                    }
                    selectRules.add(rule)
                }
            }
            appDb.replaceRuleDao.insert(*selectRules.toTypedArray())
        }.onFinally {
            finally.invoke()
        }
    }

    fun import(text: String) {
        execute {
            importAwait(text.trim())
        }.onError {
            errorLiveData.postValue("ImportError:${it.localizedMessage}")
            AppLog.put("ImportError:${it.localizedMessage}", it)
        }.onSuccess {
            comparisonSource()
        }
    }

    private suspend fun importAwait(text: String) {
        when {
            text.isAbsUrl() -> importUrl(text)
            text.isJsonArray() -> {
                val rules = ReplaceAnalyzer.jsonToReplaceRules(text).getOrThrow()
                allRules.addAll(rules)
            }

            text.isJsonObject() -> {
                val rule = ReplaceAnalyzer.jsonToReplaceRule(text).getOrThrow()
                allRules.add(rule)
            }

            text.isUri() -> {
                importAwait(text.toUri().readText(appCtx))
            }

            else -> throw NoStackTraceException("格式不对")
        }
    }

    private suspend fun importUrl(url: String) {
        okHttpClient.newCallResponseBody {
            if (url.endsWith("#requestWithoutUA")) {
                url(url.substringBeforeLast("#requestWithoutUA"))
                header(AppConst.UA_NAME, "null")
            } else {
                url(url)
            }
        }.decompressed().text("utf-8").let {
            importAwait(it)
        }
    }

    private fun comparisonSource() {
        execute {
            allRules.forEach {
                val rule = appDb.replaceRuleDao.findById(it.id)
                checkRules.add(rule)
                selectStatus.add(rule == null)
            }
        }.onSuccess {
            successLiveData.postValue(allRules.size)
        }
    }
}