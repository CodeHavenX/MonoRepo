package com.codehavenx.alpaca.frontend.appcore.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.codehavenx.alpaca.frontend.appcore.ui.components.LoadingAnimationOverlay
import org.koin.compose.koinInject

/**
 * The Home screen.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    HomeContent(
        uiState.content,
        uiState.isLoading,
    )
}

@Suppress("UnusedParameter")
@Composable
internal fun HomeContent(content: HomeUIModel, loading: Boolean) {
    LoadingAnimationOverlay(isLoading = loading)
}
