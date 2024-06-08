package com.cramsan.edifikana.client.lib.utils

import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.logging.logE
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend inline fun <T> WorkContext.getOrCatch(
    tag: String,
    crossinline block: suspend () -> T,
): Result<T> = runSuspendCatching {
    withContext(dispatcherProvider.ioDispatcher()) {
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

expect fun readBytes(uri: CoreUri): Result<ByteArray>

expect fun processImageData(data: ByteArray): Result<ByteArray>

inline fun <T, R> T.runSuspendCatching(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
