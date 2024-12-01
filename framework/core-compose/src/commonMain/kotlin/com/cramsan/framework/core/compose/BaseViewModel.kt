package com.cramsan.framework.core.compose

import androidx.lifecycle.ViewModel
import com.cramsan.framework.logging.logD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Base ViewModel class that provides a [viewModelScope] and logs when the ViewModel is created and cleared.
 */
open class BaseViewModel(
    dependencies: ViewModelDependencies,
) : ViewModel() {

    protected val viewModelScope: CoroutineScope = CoroutineScope(
        SupervisorJob() + dependencies.coroutineExceptionHandler + dependencies.dispatcherProvider.uiDispatcher()
    )

    init {
        logD(TAG, "ViewModel created: %s", this.hashCode())
    }

    override fun onCleared() {
        super.onCleared()
        logD(TAG, "ViewModel cleared: %s", this.hashCode())
        viewModelScope.cancel()
    }

    companion object {
        private const val TAG = "BaseViewModel"
    }
}
