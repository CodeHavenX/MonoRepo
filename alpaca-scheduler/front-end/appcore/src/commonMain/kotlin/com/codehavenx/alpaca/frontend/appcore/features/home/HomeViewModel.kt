package com.codehavenx.alpaca.frontend.appcore.features.home

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The ViewModel for the Home screen.
 */
class HomeViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

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
