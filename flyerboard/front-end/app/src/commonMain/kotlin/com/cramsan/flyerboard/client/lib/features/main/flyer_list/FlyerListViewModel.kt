package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the Flyer List screen.
 */
@FrontendViewModel
class FlyerListViewModel(dependencies: ViewModelDependencies, private val flyerManager: FlyerManager) :
    BaseViewModel<FlyerListEvent, FlyerListUIState>(dependencies, FlyerListUIState.Initial, TAG) {
    /**
     * Load the initial page of public flyers, optionally filtered by [query].
     */
    fun loadFlyers(query: String? = null) {
        logI(TAG, "loadFlyers query=$query")
        viewModelCoroutineScope.launch {
            updateUiState { FlyerListUIState.Loading(query = query ?: "") }
            flyerManager
                .listFlyers(query = query)
                .onSuccess { paginated ->
                    updateUiState {
                        if (paginated.flyers.isEmpty()) {
                            FlyerListUIState.Empty(query = query ?: "")
                        } else {
                            FlyerListUIState.Content(flyers = paginated.flyers, query = query ?: "")
                        }
                    }
                }.onFailure { error ->
                    updateUiState { FlyerListUIState.Error(error.message ?: "Unknown error", query = query ?: "") }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(
                            message = "Failed to load flyers: ${error.message}",
                        ),
                    )
                }
        }
    }

    /**
     * Reload the flyer list preserving the current search query.
     */
    fun refresh() {
        logI(TAG, "refresh")
        val currentQuery = uiState.value.query.takeIf { it.isNotEmpty() }
        loadFlyers(query = currentQuery)
    }

    /**
     * Update the search query and reload results.
     */
    fun onQueryChanged(q: String) {
        logI(TAG, "onQueryChanged q=$q")
        loadFlyers(query = q.takeIf { it.isNotEmpty() })
    }

    /**
     * Navigate to the detail screen for [flyerId].
     */
    fun onFlyerSelected(flyerId: FlyerId) {
        logI(TAG, "onFlyerSelected: ${flyerId.flyerId}")
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                FlyerBoardWindowsEvent.NavigateToScreen(
                    MainDestination.FlyerDetailDestination(flyerId.flyerId),
                ),
            )
        }
    }

    /**
     * Navigate to the Submit Flyer screen.
     */
    fun onSubmitFlyer() {
        logI(TAG, "onSubmitFlyer")
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                FlyerBoardWindowsEvent.NavigateToScreen(
                    MainDestination.FlyerSubmitDestination,
                ),
            )
        }
    }

    companion object {
        private const val TAG = "FlyerListViewModel"
    }
}
