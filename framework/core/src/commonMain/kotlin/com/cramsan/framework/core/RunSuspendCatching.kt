package com.cramsan.framework.core

import com.cramsan.framework.logging.logW
import kotlinx.coroutines.CancellationException

/**
 * Run a suspend function and catch any exceptions that occur. This function will log any exceptions
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
