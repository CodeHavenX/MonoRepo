package com.cramsan.edifikana.client.lib.features.base

import androidx.lifecycle.ViewModel
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

@Suppress("UnnecessaryAbstractClass")
abstract class EdifikanaBaseViewModel(
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    protected val viewModelScope: CoroutineScope = CoroutineScope(
        SupervisorJob() + exceptionHandler + dispatcherProvider.uiDispatcher()
    )

    init {
        logI(TAG, "ViewModel created: ${this.hashCode()}")
    }

    override fun onCleared() {
        super.onCleared()
        logI(TAG, "ViewModel cleared: ${this.hashCode()}")
        viewModelScope.cancel()
    }

    companion object {
        private const val TAG = "EdifikanaBaseViewModel"
    }
}
