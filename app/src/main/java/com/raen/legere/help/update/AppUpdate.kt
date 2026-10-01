package com.raen.legere.help.update

import kotlinx.coroutines.CoroutineScope

object AppUpdate {
    val giteeUpdate = UpdateChecker()
    val githubUpdate = UpdateChecker()

    class UpdateChecker {
        fun check(scope: CoroutineScope): UpdateTask = UpdateTask()
    }

    class UpdateTask {
        fun onSuccess(callback: (UpdateInfo) -> Unit): UpdateTask = this
        fun onError(callback: (Throwable) -> Unit): UpdateTask = this
        fun onFinally(callback: () -> Unit): UpdateTask = this
    }

    class UpdateInfo(
        val versionName: String = "",
        val updateLog: String = "",
        val downloadUrl: String = "",
        val backupUrl: String = ""
    )
}
