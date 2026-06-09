package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies

/**
 * ViewModel for [FeatureReplacemeScreen].
 *
 * This ViewModel is the single source of truth for the screen's UI state. It follows the
 * MVI (Model-View-Intent) pattern:
 *
 * - Produce new [FeatureReplacemeUIState] snapshots by calling [updateUiState].
 * - Fire one-shot side effects (navigation, snackbars) by calling [emitEvent].
 * - All async work must run inside [viewModelCoroutineScope] so it is cancelled automatically
 *   when the ViewModel is cleared.
 *
 * Example — loading data with a progress indicator:
 * ```
 * fun loadItems() {
 *     viewModelCoroutineScope.launch {
 *         updateUiState { it.copy(isLoading = true) }
 *         myManager.fetchItems()
 *             .onSuccess { items -> updateUiState { it.copy(isLoading = false, items = items) } }
 *             .onFailure {          updateUiState { it.copy(isLoading = false) } }
 *     }
 * }
 * ```
 *
 * Example — navigating to another screen or back (replace `<App>` with this app's window
 * events class, e.g. `ExampleAppWindowsEvent`):
 * ```
 * fun onItemClicked() {
 *     viewModelCoroutineScope.launch {
 *         emitWindowEvent(<App>WindowsEvent.NavigateToScreen(ActivityReplacemeDestination.SomeDestination))
 *     }
 * }
 *
 * fun onBackClicked() {
 *     viewModelCoroutineScope.launch {
 *         emitWindowEvent(<App>WindowsEvent.NavigateBack)
 *     }
 * }
 * ```
 * `emitWindowEvent` is provided by [BaseViewModel]. Use [FeatureReplacemeEvent] / [emitEvent]
 * only for local one-shot side effects (snackbars, dialogs) — not for navigation.
 *
 * Registration checklist:
 * - TODO: Add `viewModelOf(::FeatureReplacemeViewModel)` to ViewModelModule.kt (or the
 *         platform-specific ViewModelPlatformModule.kt).
 *
 * @see FeatureReplacemeUIState for the state model
 * @see FeatureReplacemeEvent for one-shot events
 */
@FrontendViewModel
class FeatureReplacemeViewModel(
    dependencies: ViewModelDependencies,
    // TODO: Inject the managers/services this feature needs, e.g.:
    //   private val myManager: MyManager,
) : BaseViewModel<FeatureReplacemeEvent, FeatureReplacemeUIState>(dependencies, FeatureReplacemeUIState.Initial, TAG) {

    // TODO: Add one public function per user action. Each should either:
    //   - call updateUiState { it.copy(...) } to change what the screen shows, or
    //   - call emitEvent(FeatureReplacemeEvent.SomeEvent) for one-shot side effects.

    companion object {
        private const val TAG = "FeatureReplacemeViewModel"
    }
}
