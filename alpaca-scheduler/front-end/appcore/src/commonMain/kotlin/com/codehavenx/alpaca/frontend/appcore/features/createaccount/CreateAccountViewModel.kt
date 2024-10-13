package com.codehavenx.alpaca.frontend.appcore.features.createaccount

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The ViewModel for the Create Account screen.
 */
class CreateAccountViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        CreateAccountUIState(
            content = CreateAccountUIModel("", ""),
            isLoading = false,
        )
    )
    val uiState: StateFlow<CreateAccountUIState> = _uiState

    private val _event = MutableSharedFlow<CreateAccountEvent>()
    val event: SharedFlow<CreateAccountEvent> = _event
}
