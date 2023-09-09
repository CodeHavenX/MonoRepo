package com.cramsan.framework.core

import kotlinx.coroutines.Dispatchers

/**
 * JVM implementation of [DispatcherProvider]. It uses [Dispatchers.IO] for [ioDispatcher].
 */
class BEDispatcherProvider : DispatcherProvider {

    @Suppress("InjectDispatcher")
    override fun ioDispatcher() = Dispatchers.IO

    override fun uiDispatcher() = throw NotImplementedError("BE platforms cannot access a UI dispatcher.")
}
