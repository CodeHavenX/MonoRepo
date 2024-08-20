package com.cramsan.framework.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

/**
 * Common-code interface for all viewmodels. This interface is expected to be implemented in common
 * code with a platform-specific shim.
 */
interface BaseViewModel {

    /**
     * The tag to use for logging.
     */
    val logTag: String

    /**
     * The dispatcher provider to use for this viewmodel.
     */
    val dispatcherProvider: DispatcherProvider

    /**
     * The coroutine scope to use for this viewmodel.
     */
    val viewModelScope: CoroutineScope

    /**
     * A flow of events that this viewmodel emits to the UI.
     */
    val events: SharedFlow<BaseEvent>

    /**
     * Called when this viewmodel will not longer be used and all resources can be released. All
     * calls to this VM after this function is called will have undefined behaviour.
     */
    fun close()
}
