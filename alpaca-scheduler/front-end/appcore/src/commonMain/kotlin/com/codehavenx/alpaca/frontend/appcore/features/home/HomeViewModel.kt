package com.codehavenx.alpaca.frontend.appcore.features.home

import com.codehavenx.alpaca.frontend.appcore.features.base.AlpacaViewModel
import com.codehavenx.alpaca.frontend.appcore.managers.WorkContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The ViewModel for the Home screen.
 */
class HomeViewModel(
    workContext: WorkContext,
) : AlpacaViewModel(workContext) {

    private val _uiState = MutableStateFlow(
        HomeUIState(
            content = HomeUIModel(""),
            isLoading = false,
        )
    )
    val uiState: StateFlow<HomeUIState> = _uiState

    private val _event = MutableSharedFlow<HomeEvent>()
    val event: SharedFlow<HomeEvent> = _event
}
