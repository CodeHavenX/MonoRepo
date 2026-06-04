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
 * Feature screen.
 *
 * Observes [FeatureReplacemeUIState] from [FeatureReplacemeViewModel] and forwards user
 * interactions back to the ViewModel as function calls. Replace this body with your
 * feature's UI.
 */
@Composable
fun FeatureReplacemeScreen(
    viewModel: FeatureReplacemeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            FeatureReplacemeEvent.Noop -> Unit
        }
    }

    FeatureReplacemeContent(uiState = uiState)
}

@Composable
internal fun FeatureReplacemeContent(
    uiState: FeatureReplacemeUIState,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            // TODO: Add your feature's Compose content here
            LoadingAnimationOverlay(uiState.isLoading)
        }
    }
}
