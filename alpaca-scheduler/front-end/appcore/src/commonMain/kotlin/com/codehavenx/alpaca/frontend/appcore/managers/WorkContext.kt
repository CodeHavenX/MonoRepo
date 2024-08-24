package com.codehavenx.alpaca.frontend.appcore.managers

import com.cramsan.framework.core.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock

/**
 * Aggregator class that holds several common dependencies for the app. This class is expected to be used within the
 * business logic domain.
 */
data class WorkContext(
    val clock: Clock,
    val appScope: CoroutineScope,
    val dispatcherProvider: DispatcherProvider,
    val coroutineExceptionHandler: CoroutineExceptionHandler,
)
