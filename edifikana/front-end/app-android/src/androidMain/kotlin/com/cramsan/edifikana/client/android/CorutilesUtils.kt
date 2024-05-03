package com.cramsan.edifikana.client.android

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

suspend fun <T> CoroutineDispatcher.run(block: suspend () -> T):  Result<T> = runCatching {
    withContext(this) {
        block()
    }
}