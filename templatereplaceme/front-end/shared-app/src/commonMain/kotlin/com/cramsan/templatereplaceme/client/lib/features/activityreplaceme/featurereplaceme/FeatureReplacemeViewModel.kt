package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies

/**
 * ViewModel for the [FeatureReplacemeScreen].
 *
 * Inject the managers and services this feature needs via the constructor, then
 * implement domain-specific actions as functions that call [updateUiState] or [emitEvent].
 *
 * @see FeatureReplacemeUIState for the state model
 * @see FeatureReplacemeEvent for one-shot events
 */
@FrontendViewModel
class FeatureReplacemeViewModel(
    dependencies: ViewModelDependencies,
    // TODO: Inject domain managers needed by this feature
) : BaseViewModel<FeatureReplacemeEvent, FeatureReplacemeUIState>(dependencies, FeatureReplacemeUIState.Initial, TAG) {
    // TODO: Add domain-specific actions here as functions that call updateUiState or emitEvent

    companion object {
        private const val TAG = "FeatureReplacemeViewModel"
    }
}
