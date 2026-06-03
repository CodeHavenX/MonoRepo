package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import org.koin.compose.viewmodel.koinViewModel

/**
 * Entry-point composable for the FeatureReplaceme screen.
 *
 * This function is responsible for wiring: it injects the ViewModel, collects state,
 * and dispatches one-shot [FeatureReplacemeEvent]s. All actual UI rendering is delegated
 * to [FeatureReplacemeContent] so that it stays testable and preview-friendly.
 *
 * Navigation / wiring checklist:
 * - TODO: Register this screen as a `composable(FeatureReplacemeDestination::class)` entry
 *         inside the parent activity's nav graph builder (ActivityReplacemeActivityScreen.kt).
 * - TODO: Handle navigation events in the `ObserveViewModelEvents` block below.
 */
@Composable
fun FeatureReplacemeScreen(
    viewModel: FeatureReplacemeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            // TODO: Replace Noop with real event handling, e.g.:
            //   is FeatureReplacemeEvent.NavigateToDetails -> navController.navigate(...)
            FeatureReplacemeEvent.Noop -> Unit
        }
    }

    FeatureReplacemeContent(uiState = uiState)
}

/**
 * Pure UI composable for the FeatureReplaceme screen.
 *
 * Receives state and callbacks only — no ViewModel injection, no side effects.
 * This separation makes it easy to preview and unit-test the layout independently.
 *
 * TODO: Replace the placeholder Box with your feature's real Compose layout.
 *       Add callback parameters (e.g. `onItemClicked: (String) -> Unit`) for each
 *       user interaction and wire them to ViewModel calls in [FeatureReplacemeScreen].
 */
@Composable
internal fun FeatureReplacemeContent(
    uiState: FeatureReplacemeUIState,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            // TODO: Add your feature's Compose content here.
            LoadingAnimationOverlay(uiState.isLoading)
        }
    }
}
