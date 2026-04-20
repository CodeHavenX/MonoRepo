package com.cramsan.flyerboard.client.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.cramsan.flyerboard.client.lib.features.application.FlyerBoardApplicationViewModel
import com.cramsan.flyerboard.client.lib.features.application.FlyerBoardJvmMainScreenEventHandler
import com.cramsan.flyerboard.client.lib.features.window.ComposableKoinContext
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowScreen
import org.koin.compose.koinInject
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Main function for the desktop application.
 */
@OptIn(KoinExperimentalAPI::class)
fun main() = application {
    ComposableKoinContext {
        val processViewModel: FlyerBoardApplicationViewModel = koinInject()
        val eventHandler = remember { FlyerBoardJvmMainScreenEventHandler() }
        val appState by processViewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            processViewModel.initialize()
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = "FlyerBoard",
            state = rememberWindowState(
                size = DpSize(600.dp, 800.dp)
            )
        ) {
            KoinScope<String>("root-window") {
                FlyerBoardWindowScreen(
                    eventHandler = eventHandler,
                )
            }
        }

        if (appState.showDebugWindow) {
            Window(
                onCloseRequest = { processViewModel.setShowDebugWindow(false) },
                title = "Debug Window - FlyerBoard",
                state = rememberWindowState(
                    size = DpSize(400.dp, 600.dp)
                )
            ) {
                KoinScope<String>("debug-window") {
                    FlyerBoardWindowScreen(
                        eventHandler = eventHandler,
                    )
                }
            }
        }
    }
}
