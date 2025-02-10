package com.cramsan.framework.core.compose

import androidx.lifecycle.ViewModel
import com.cramsan.framework.logging.logD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Base ViewModel class that provides a [viewModelScope] and logs when the ViewModel is created and cleared.
 */
@Suppress("UNCHECKED_CAST")
open class BaseViewModel<E : ViewModelEvent, UI : ViewModelUIState> (
    dependencies: ViewModelDependencies,
    initialState: UI,
    private val tag: String,
) : ViewModel() {

    protected val viewModelScope: CoroutineScope = CoroutineScope(
        SupervisorJob() + dependencies.coroutineExceptionHandler + dependencies.dispatcherProvider.uiDispatcher()
    )

    private val _uiState = MutableStateFlow(initialState)

    /**
     * UI state of the screen.
     */
    val uiState: StateFlow<UI>
        get() = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<E>()

    /**
     * Event flow to be observed.
     */
    val events: SharedFlow<E>
        get() = _events.asSharedFlow()

    init {
        logD(tag, "ViewModel created: %s", this.hashCode())
    }

    override fun onCleared() {
        super.onCleared()
        logD(tag, "ViewModel cleared: %s", this.hashCode())
        viewModelScope.cancel()
    }

    protected suspend fun emitEvent(event: E) {
        _events.emit(event)
    }

    protected fun updateUiState(block: (UI) -> UI) {
        _uiState.value = block(_uiState.value)
    }
}
