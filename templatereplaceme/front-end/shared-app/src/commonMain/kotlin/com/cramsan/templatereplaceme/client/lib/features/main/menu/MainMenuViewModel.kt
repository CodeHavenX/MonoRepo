package com.cramsan.templatereplaceme.client.lib.features.main.menu

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies

/**
 * ViewModel for the [MainMenuScreen].
 *
 * Inject the managers and services this feature needs via the constructor, then
 * implement domain-specific actions as functions that call [updateUiState] or [emitEvent].
 *
 * @see MainMenuUIState for the state model
 * @see MainMenuEvent for one-shot events
 */
@FrontendViewModel
class MainMenuViewModel(
    dependencies: ViewModelDependencies,
    // TODO: Inject domain managers needed by this feature
) : BaseViewModel<MainMenuEvent, MainMenuUIState>(dependencies, MainMenuUIState.Initial, TAG) {
    // TODO: Add domain-specific actions here as functions that call updateUiState or emitEvent

    companion object {
        private const val TAG = "MainMenuViewModel"
    }
}
