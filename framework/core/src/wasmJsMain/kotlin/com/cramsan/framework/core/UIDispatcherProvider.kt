package com.cramsan.framework.core

import kotlinx.coroutines.Dispatchers

/**
 * Wasm Implementation of a DispatcherProvider. This implementation is expected to be used for a wasm web app.
 */
class UIDispatcherProvider : DispatcherProvider {

    @Suppress("InjectDispatcher")
    override fun ioDispatcher() = Dispatchers.Main

    override fun uiDispatcher() = Dispatchers.Main
}
