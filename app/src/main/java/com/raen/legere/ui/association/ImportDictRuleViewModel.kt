package com.raen.legere.ui.association

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.raen.legere.R
import com.raen.legere.base.BaseViewModel
import com.raen.legere.constant.AppConst
import com.raen.legere.constant.AppLog
import com.raen.legere.data.appDb
import com.raen.legere.data.entities.DictRule
import com.raen.legere.exception.NoStackTraceException
import com.raen.legere.help.http.decompressed
import com.raen.legere.help.http.newCallResponseBody
import com.raen.legere.help.http.okHttpClient
import com.raen.legere.help.http.text
import com.raen.legere.utils.GSON
import com.raen.legere.utils.fromJsonArray
import com.raen.legere.utils.fromJsonObject
import com.raen.legere.utils.isAbsUrl
import com.raen.legere.utils.isJsonArray
import com.raen.legere.utils.isJsonObject
import com.raen.legere.utils.isUri
import com.raen.legere.utils.readText
import splitties.init.appCtx

class ImportDictRuleViewModel(app: Application) : BaseViewModel(app) {

    val errorLiveData = MutableLiveData<String>()
    val successLiveData = MutableLiveData<Int>()

    val allSources = arrayListOf<DictRule>()
    val checkSources = arrayListOf<DictRule?>()
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
            val selectSource = arrayListOf<DictRule>()
            selectStatus.forEachIndexed { index, b ->
                if (b) {
                    selectSource.add(allSources[index])
                }
            }
            appDb.dictRuleDao.insert(*selectSource.toTypedArray())
        }.onFinally {
            finally.invoke()
        }
    }

    fun importSource(text: String) {
        execute {
            importSourceAwait(text.trim())
        }.onError {
            errorLiveData.postValue("ImportError:${it.localizedMessage}")
            AppLog.put("ImportError:${it.localizedMessage}", it)
        }.onSuccess {
            comparisonSource()
        }
    }

    private suspend fun importSourceAwait(text: String) {
        when {
            text.isJsonObject() -> {
                GSON.fromJsonObject<DictRule>(text).getOrThrow().let {
                    allSources.add(it)
                }
            }

            text.isJsonArray() -> GSON.fromJsonArray<DictRule>(text).getOrThrow().let { items ->
                allSources.addAll(items)
            }

            text.isAbsUrl() -> {
                importSourceUrl(text)
            }

            text.isUri() -> {
                importSourceAwait(text.toUri().readText(appCtx))
            }

            else -> throw NoStackTraceException(context.getString(R.string.wrong_format))
        }
    }

    private suspend fun importSourceUrl(url: String) {
        okHttpClient.newCallResponseBody {
            if (url.endsWith("#requestWithoutUA")) {
                url(url.substringBeforeLast("#requestWithoutUA"))
                header(AppConst.UA_NAME, "null")
            } else {
                url(url)
            }
        }.decompressed().text().let {
            importSourceAwait(it)
        }
    }

    private fun comparisonSource() {
        execute {
            allSources.forEach {
                val source = appDb.dictRuleDao.getByName(it.name)
                checkSources.add(source)
                selectStatus.add(source == null)
            }
            successLiveData.postValue(allSources.size)
        }
    }

}