package com.cramsan.edifikana.client.android.utils

import com.cramsan.edifikana.client.android.managers.WorkContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend inline fun <T> WorkContext.getOrCatch(crossinline block: suspend () -> T): Result<T> = runCatching {
    withContext(backgroundDispatcher) {
        block()
    }
}

fun WorkContext.launch(block: suspend CoroutineScope.() -> Unit): Job {
    return appScope.launch(
        context = coroutineExceptionHandler,
        block = block,
    )
}