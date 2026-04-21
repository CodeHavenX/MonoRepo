package com.cramsan.flyerboard.client.lib.features.main.flyer_edit

import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the Flyer Edit screen.
 */
class FlyerEditViewModel(
    dependencies: ViewModelDependencies,
    private val flyerManager: FlyerManager,
) : BaseViewModel<FlyerEditEvent, FlyerEditUIState>(dependencies, FlyerEditUIState.Initial, TAG) {

    /**
     * Load the flyer identified by [flyerIdValue] and populate the form fields.
     */
    fun loadFlyer(flyerIdValue: String) {
        logI(TAG, "loadFlyer: $flyerIdValue")
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, errorMessage = null) }
            flyerManager.getFlyer(FlyerId(flyerIdValue))
                .onSuccess { flyer ->
                    if (flyer == null) {
                        updateUiState { it.copy(isLoading = false) }
                        emitWindowEvent(FlyerBoardWindowsEvent.ShowSnackbar(message = "Flyer not found."))
                    } else {
                        updateUiState {
                            it.copy(
                                isLoading = false,
                                title = flyer.title,
                                description = flyer.description,
                                expiresAt = flyer.expiresAt,
                            )
                        }
                    }
                }
                .onFailure { error ->
                    updateUiState { it.copy(isLoading = false, errorMessage = error.message) }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(message = "Failed to load flyer: ${error.message}"),
                    )
                }
        }
    }

    /**
     * Update the title field in the UI state.
     */
    fun onTitleChanged(title: String) {
        viewModelScope.launch { updateUiState { it.copy(title = title) } }
    }

    /**
     * Update the description field in the UI state.
     */
    fun onDescriptionChanged(description: String) {
        viewModelScope.launch { updateUiState { it.copy(description = description) } }
    }

    /**
     * Update the expiresAt field in the UI state.
     */
    fun onExpiresAtChanged(expiresAt: String) {
        viewModelScope.launch { updateUiState { it.copy(expiresAt = expiresAt.takeIf { it.isNotBlank() }) } }
    }

    /**
     * Submit the edited flyer fields for [flyerIdValue].
     */
    fun saveFlyer(flyerIdValue: String) {
        logI(TAG, "saveFlyer: $flyerIdValue")
        viewModelScope.launch {
            val state = uiState.value
            updateUiState { it.copy(isSaving = true, errorMessage = null) }
            flyerManager.updateFlyer(
                flyerId = FlyerId(flyerIdValue),
                title = state.title,
                description = state.description,
                expiresAt = state.expiresAt,
            )
                .onSuccess {
                    updateUiState { it.copy(isSaving = false) }
                    emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
                }
                .onFailure { error ->
                    updateUiState { it.copy(isSaving = false, errorMessage = error.message) }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(message = "Failed to save flyer: ${error.message}"),
                    )
                }
        }
    }

    /**
     * Navigate back without saving.
     */
    fun navigateBack() {
        logI(TAG, "navigateBack")
        viewModelScope.launch {
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "FlyerEditViewModel"
    }
}
