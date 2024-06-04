package com.cramsan.edifikana.client.android.utils

import com.cramsan.edifikana.client.android.managers.WorkContext
import com.cramsan.framework.logging.logE
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend inline fun <T> WorkContext.getOrCatch(
    tag: String,
    crossinline block: suspend () -> T,
): Result<T> = runCatching {
    withContext(backgroundDispatcher) {
        block()
    }
}.onFailure {
    logE(tag, "Operation failed. ", it)
}

fun WorkContext.launch(tag: String, block: suspend CoroutineScope.() -> Unit): Job {
    return appScope.launch(
        context = coroutineExceptionHandler + CoroutineName(tag),
        block = block,
    )
}
