package com.cramsan.edifikana.client.lib.utils

import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logE
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Get a result of a suspend function or catch the exception.
 */
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

/**
 * Launch a coroutine with a tag.
 */
fun WorkContext.launch(tag: String, block: suspend CoroutineScope.() -> Unit): Job {
    return appScope.launch(
        context = coroutineExceptionHandler + CoroutineName(tag),
        block = block,
    )
}

/**
 * I/O dependencies.
 */
expect class IODependencies

/**
 * Read bytes from a URI.
 */
expect fun readBytes(uri: CoreUri, dependencies: IODependencies): Result<ByteArray>

/**
 * Process image data.
 */
expect fun processImageData(data: ByteArray): Result<ByteArray>
