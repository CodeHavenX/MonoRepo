package com.cramsan.framework.core

import com.cramsan.framework.logging.logE
import kotlinx.coroutines.withContext

/**
 * Perform a suspend operation. Return a [Result] with the value in cases of success or the exception if one is thrown.
 * This function will automatically log exceptions encountered during the operation.
 *
 * If you do not need automatic context switching, consider using [runSuspendCatching] instead.
 */
suspend inline fun <T> ManagerDependencies.getOrCatch(tag: String, crossinline block: suspend () -> T): Result<T> =
    runSuspendCatching(tag) {
        withContext(dispatcherProvider.ioDispatcher()) {
            block()
        }
    }.onFailure {
        logE(tag, "Operation failed. ", it)
    }
