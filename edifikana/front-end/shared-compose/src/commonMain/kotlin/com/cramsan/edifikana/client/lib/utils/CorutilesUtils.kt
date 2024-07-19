package com.cramsan.edifikana.client.lib.utils

import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend inline fun <T> WorkContext.getOrCatch(
    tag: String,
    crossinline block: suspend () -> T,
): Result<T> = runSuspendCatching(tag) {
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

expect class IODependencies

expect fun readBytes(uri: CoreUri, dependencies: IODependencies): Result<ByteArray>

expect fun processImageData(data: ByteArray): Result<ByteArray>

inline fun <T, R> T.runSuspendCatching(tag: String, block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        logW(tag, "Operation failed. ", e)
        Result.failure(e)
    }
}
