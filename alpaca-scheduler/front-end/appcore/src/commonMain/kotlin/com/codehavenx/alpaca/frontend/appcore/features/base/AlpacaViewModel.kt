package com.codehavenx.alpaca.frontend.appcore.features.base

import androidx.lifecycle.ViewModel
import com.codehavenx.alpaca.frontend.appcore.managers.WorkContext
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Base ViewModel class that provides a some common functionality across all [ViewModel]s.
 */
abstract class AlpacaViewModel(
    workContext: WorkContext,
) : ViewModel() {

    protected val viewModelScope: CoroutineScope = CoroutineScope(
        SupervisorJob() + workContext.coroutineExceptionHandler + workContext.dispatcherProvider.uiDispatcher()
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
        private const val TAG = "AlpacaViewModel"
    }
}
