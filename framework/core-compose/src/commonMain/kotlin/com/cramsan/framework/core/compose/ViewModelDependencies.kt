package com.cramsan.framework.core.compose

import com.cramsan.framework.core.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope

/**
 * Aggregator class that holds several common dependencies for the [BaseViewModel].
 */
data class ViewModelDependencies(
    val appScope: CoroutineScope,
    val dispatcherProvider: DispatcherProvider,
    val coroutineExceptionHandler: CoroutineExceptionHandler,
    val windowEventReceiver: EventReceiver<WindowEvent>,
    val applicationEventReceiver: EventReceiver<ApplicationEvent>,
)
