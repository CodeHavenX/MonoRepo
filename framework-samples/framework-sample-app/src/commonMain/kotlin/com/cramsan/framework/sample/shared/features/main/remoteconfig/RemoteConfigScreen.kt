package com.cramsan.framework.sample.shared.features.main.remoteconfig

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * RemoteConfig screen.
 *
 * Demonstrates all [com.cramsan.framework.remoteconfig.RemoteConfig] API methods
 * using a [com.cramsan.framework.sample.shared.stubs.SampleRemoteConfigPayload] as the type parameter.
 */
@Composable
fun RemoteConfigScreen(
    viewModel: RemoteConfigViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            RemoteConfigEvent.Noop -> Unit
        }
    }

    RemoteConfigContent(
        uiState = uiState,
        onCheckIsReady = { viewModel.checkIsPayloadReady() },
        onDownloadPayload = { viewModel.downloadPayload() },
        onDownloadAsync = { viewModel.downloadAsync() },
        onGetOrNull = { viewModel.getPayloadOrNull() },
        onGetOrDefault = { viewModel.getPayloadOrDefault() },
        onBack = { viewModel.navigateBack() },
    )
}

/**
 * Content of the RemoteConfig screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RemoteConfigContent(
    uiState: RemoteConfigUIState,
    onCheckIsReady: () -> Unit,
    onDownloadPayload: () -> Unit,
    onDownloadAsync: () -> Unit,
    onGetOrNull: () -> Unit,
    onGetOrDefault: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Remote Config") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            sectionContent = { modifier ->
                Text("Payload ready: ${uiState.isPayloadReady}")
                Text("Payload: ${uiState.payloadInfo}")
                Text("Last action: ${uiState.lastAction}")
                Button(onClick = onCheckIsReady, modifier = modifier) { Text("isConfigPayloadReady()") }
                Button(onClick = onDownloadPayload, modifier = modifier) { Text("downloadConfigPayload()") }
                Button(onClick = onDownloadAsync, modifier = modifier) { Text("downloadConfigPayloadAsync()") }
                Button(onClick = onGetOrNull, modifier = modifier) { Text("getConfigPayloadOrNull()") }
                Button(onClick = onGetOrDefault, modifier = modifier) { Text("getConfigPayloadOrDefault()") }
            },
            overlay = {
                LoadingAnimationOverlay(isLoading = uiState.isLoading)
            },
        )
    }
}
