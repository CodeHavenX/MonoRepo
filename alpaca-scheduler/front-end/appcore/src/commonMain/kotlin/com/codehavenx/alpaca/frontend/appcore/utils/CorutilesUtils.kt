package com.codehavenx.alpaca.frontend.appcore.utils

import com.codehavenx.alpaca.frontend.appcore.managers.WorkContext
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

/**
 * Perform a suspend operation. Return a [Result] with the value in cases of success or the exception if one is thrown.
 * This function will automatically log exceptions encountered during the operation. This function is meant to be used
 * within a [WorkContext] to automatically handle the context switching to a background thread.
 *
 * If you do not need automatic context switching, consider using [runSuspendCatching] instead.
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
 * Perform a suspend operation. Return a [Result] with the value in cases of success or the exception if one is thrown.
 * This function will automatically log exceptions encountered during the operation.
 */
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
