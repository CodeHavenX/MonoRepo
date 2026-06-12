package com.cramsan.flyerboard.client.lib.features.main.archive

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
 * ViewModel for the Archive screen.
 */
@FrontendViewModel
class ArchiveViewModel(dependencies: ViewModelDependencies, private val flyerManager: FlyerManager) :
    BaseViewModel<ArchiveEvent, ArchiveUIState>(dependencies, ArchiveUIState.Initial, TAG) {
    /**
     * Load archived flyers, optionally filtered by [query].
     */
    fun loadFlyers(query: String? = null) {
        logI(TAG, "loadFlyers query=$query")
        viewModelCoroutineScope.launch {
            updateUiState { ArchiveUIState.Loading(query = query ?: "") }
            flyerManager
                .listArchived(query = query)
                .onSuccess { paginated ->
                    updateUiState {
                        if (paginated.flyers.isEmpty()) {
                            ArchiveUIState.Empty(query = query ?: "")
                        } else {
                            ArchiveUIState.Content(flyers = paginated.flyers, query = query ?: "")
                        }
                    }
                }.onFailure { error ->
                    updateUiState { ArchiveUIState.Error(query = query ?: "") }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(
                            message = "Failed to load archive: ${error.message}",
                        ),
                    )
                }
        }
    }

    /**
     * Reload the archive preserving the current search query.
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
     * Navigate back.
     */
    fun navigateBack() {
        logI(TAG, "navigateBack")
        viewModelCoroutineScope.launch {
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "ArchiveViewModel"
    }
}
