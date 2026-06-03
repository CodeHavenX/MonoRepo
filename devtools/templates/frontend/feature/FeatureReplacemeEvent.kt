package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * One-shot events emitted by [FeatureReplacemeViewModel] and consumed by [FeatureReplacemeScreen].
 *
 * Use events for side effects that must happen exactly once and should NOT live in
 * [FeatureReplacemeUIState] (navigation, showing a snackbar, opening a dialog, etc.).
 *
 * The screen handles these in its `ObserveViewModelEvents` block:
 * ```
 * ObserveViewModelEvents(viewModel) { event ->
 *     when (event) {
 *         is FeatureReplacemeEvent.NavigateToDetails -> navController.navigate(...)
 *         FeatureReplacemeEvent.ShowSuccessSnackbar  -> snackbarHost.showSnackbar(...)
 *     }
 * }
 * ```
 *
 * TODO: Replace `Noop` with the real events this feature needs, e.g.:
 * ```
 * data class NavigateToDetails(val id: String) : FeatureReplacemeEvent()
 * data object ShowSuccessSnackbar : FeatureReplacemeEvent()
 * data object NavigateBack : FeatureReplacemeEvent()
 * ```
 */
sealed class FeatureReplacemeEvent : ViewModelEvent {
    /** Placeholder — remove once real events are added. */
    data object Noop : FeatureReplacemeEvent()
}
