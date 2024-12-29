package com.cramsan.edifikana.client.lib.features.root.admin.property

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for the Property screen.
 **/
class PropertyViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(
        PropertyUIState(
            content = null,
            isLoading = true,
        )
    )

    /**
     * UI state of the screen.
     */
    val uiState: StateFlow<PropertyUIState> = _uiState

    private val _event = MutableSharedFlow<PropertyEvent>()

    /**
     * Event flow to be observed.
     */
    val event: SharedFlow<PropertyEvent> = _event
}
