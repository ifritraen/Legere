package com.raen.legere.ui.association

import android.app.Application
import androidx.core.net.toUri
import com.raen.legere.R
import com.raen.legere.constant.AppConst
import com.raen.legere.help.config.ReadBookConfig
import com.raen.legere.help.http.decompressed
import com.raen.legere.help.http.newCallResponseBody
import com.raen.legere.help.http.okHttpClient
import com.raen.legere.help.http.text
import com.raen.legere.utils.FileUtils
import com.raen.legere.utils.externalCache
import okhttp3.MediaType.Companion.toMediaType
import splitties.init.appCtx

class OnLineImportViewModel(app: Application) : BaseAssociationViewModel(app) {

    fun getText(url: String, success: (text: String) -> Unit) {
        execute {
            okHttpClient.newCallResponseBody {
                if (url.endsWith("#requestWithoutUA")) {
                    url(url.substringBeforeLast("#requestWithoutUA"))
                    header(AppConst.UA_NAME, "null")
                } else {
                    url(url)
                }
            }.decompressed().text("utf-8")
        }.onSuccess {
            success.invoke(it)
        }.onError {
            errorLive.postValue(
                it.localizedMessage ?: context.getString(R.string.unknown_error)
            )
        }
    }

    fun getBytes(url: String, success: (bytes: ByteArray) -> Unit) {
        execute {
            okHttpClient.newCallResponseBody {
                if (url.endsWith("#requestWithoutUA")) {
                    url(url.substringBeforeLast("#requestWithoutUA"))
                    header(AppConst.UA_NAME, "null")
                } else {
                    url(url)
                }
            }.bytes()
        }.onSuccess {
            success.invoke(it)
        }.onError {
            errorLive.postValue(
                it.localizedMessage ?: context.getString(R.string.unknown_error)
            )
        }
    }

    fun importReadConfig(bytes: ByteArray, finally: (title: String, msg: String) -> Unit) {
        execute {
            val config = ReadBookConfig.import(bytes)
            ReadBookConfig.configList.forEachIndexed { index, c ->
                if (c.name == config.name) {
                    ReadBookConfig.configList[index] = config
                    return@execute config.name
                }
                ReadBookConfig.configList.add(config)
                return@execute config.name
            }
        }.onSuccess {
            finally.invoke(context.getString(R.string.success), "导入排版成功")
        }.onError {
            finally.invoke(
                context.getString(R.string.error),
                it.localizedMessage ?: context.getString(R.string.unknown_error)
            )
        }
    }

    fun determineType(url: String, finally: (title: String, msg: String) -> Unit) {
        execute {
            val rs = okHttpClient.newCallResponseBody {
                if (url.endsWith("#requestWithoutUA")) {
                    url(url.substringBeforeLast("#requestWithoutUA"))
                    header(AppConst.UA_NAME, "null")
                } else {
                    url(url)
                }
            }
            when (rs.contentType()) {
                "application/zip".toMediaType(),
                "application/octet-stream".toMediaType() -> {
                    importReadConfig(rs.bytes(), finally)
                }
                else -> {
                    val inputStream = rs.byteStream()
                    val file = FileUtils.createFileIfNotExist(
                        appCtx.externalCache,
                        "download",
                        "scheme_import_cache.json"
                    )
                    file.outputStream().use { out ->
                        inputStream.use {
                            it.copyTo(out)
                        }
                    }
                    importJson(file.toUri())
                }
            }
        }
    }

}