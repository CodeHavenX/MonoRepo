package com.cramsan.framework.core

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Unified implementation of [DispatcherProvider] that uses the same dispatcher for both IO and UI operations.
 */
class UnifiedDispatcherProvider(private val coroutineDispatcher: CoroutineDispatcher) : DispatcherProvider {

    override fun ioDispatcher() = coroutineDispatcher

    override fun uiDispatcher() = coroutineDispatcher
}
