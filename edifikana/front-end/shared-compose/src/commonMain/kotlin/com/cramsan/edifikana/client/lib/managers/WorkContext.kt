package com.cramsan.edifikana.client.lib.managers

import com.cramsan.framework.core.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock

data class WorkContext(
    val clock: Clock,
    val appScope: CoroutineScope,
    val dispatcherProvider: DispatcherProvider,
    val coroutineExceptionHandler: CoroutineExceptionHandler,
    val storageBucket: String,
)
