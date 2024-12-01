package com.cramsan.framework.core

import kotlinx.coroutines.CoroutineScope

/**
 * Aggregator class that holds several common dependencies for a manager class
 */
data class ManagerDependencies(
    val appScope: CoroutineScope,
    val dispatcherProvider: DispatcherProvider,
)
