package com.cramsan.edifikana.client.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.cramsan.edifikana.client.lib.features.application.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.application.EdifikanaJvmMainScreenEventHandler
import com.cramsan.edifikana.client.lib.features.window.ComposableKoinContext
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowScreen
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowViewModel
import org.koin.compose.koinInject
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Main function for the desktop application.
 */
@OptIn(KoinExperimentalAPI::class)
fun main() = application {
    ComposableKoinContext {
        val processViewModel: EdifikanaApplicationViewModel = koinInject()
        val appState by processViewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            processViewModel.initialize()
        }

        if (appState.applicationLoaded) {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Edifikana",
                state = rememberWindowState(
                    size = DpSize(600.dp, 800.dp)
                )
            ) {
                KoinScope<String>("root-window") {
                    val windowViewModel: EdifikanaWindowViewModel = koinInject()
                    val scope = rememberCoroutineScope()

                    // Create event handler with photo picker callback
                    val windowEventHandler = remember(scope) {
                        EdifikanaJvmMainScreenEventHandler(
                            scope = scope,
                            onPhotoPickerResult = { uris ->
                                windowViewModel.handleReceivedImages(uris)
                            }
                        )
                    }
                    EdifikanaWindowScreen(
                        eventHandler = windowEventHandler,
                        viewModel = windowViewModel,
                    )
                }
            }
        }
    }
}
