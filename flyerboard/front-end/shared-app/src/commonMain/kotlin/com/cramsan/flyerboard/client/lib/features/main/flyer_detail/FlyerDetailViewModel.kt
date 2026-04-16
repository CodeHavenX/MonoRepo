package com.cramsan.flyerboard.client.lib.features.main.flyer_detail

import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import kotlinx.coroutines.launch

/**
 * ViewModel for the Flyer Detail screen.
 */
class FlyerDetailViewModel(
    dependencies: ViewModelDependencies,
    private val flyerManager: FlyerManager,
) : BaseViewModel<FlyerDetailEvent, FlyerDetailUIState>(dependencies, FlyerDetailUIState.Initial, TAG) {

    /**
     * Load the flyer identified by [flyerIdValue].
     */
    fun loadFlyer(flyerIdValue: String) {
        logI(TAG, "loadFlyer: %s", flyerIdValue)
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            flyerManager.getFlyer(FlyerId(flyerIdValue))
                .onSuccess { flyer ->
                    updateUiState { it.copy(isLoading = false, flyer = flyer) }
                    if (flyer == null) {
                        emitWindowEvent(
                            FlyerBoardWindowsEvent.ShowSnackbar(
                                message = "Flyer not found.",
                            ),
                        )
                    }
                }
                .onFailure { error ->
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(
                            message = "Failed to load flyer: ${error.message}",
                        ),
                    )
                }
        }
    }

    /**
     * Navigate back to the flyer list.
     */
    fun navigateBack() {
        logI(TAG, "navigateBack")
        viewModelScope.launch {
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "FlyerDetailViewModel"
    }
}
