package com.cramsan.edifikana.client.lib.features.base

import androidx.lifecycle.ViewModel
import com.cramsan.framework.core.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

@Suppress("UnnecessaryAbstractClass")
abstract class EdifikanaBaseViewModel(
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val viewModelScope: CoroutineScope = CoroutineScope(
        SupervisorJob() + exceptionHandler + dispatcherProvider.uiDispatcher()
    )

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
