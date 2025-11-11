package com.cramsan.templatereplaceme.client.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeApplicationViewModel
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeJvmMainScreenEventHandler
import com.cramsan.templatereplaceme.client.lib.features.window.ComposableKoinContext
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowScreen
import org.koin.compose.koinInject
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Main function for the desktop application.
 */
@OptIn(KoinExperimentalAPI::class)
fun main() = application {
    ComposableKoinContext {
        val processViewModel: TemplateReplaceMeApplicationViewModel = koinInject()
        val eventHandler = remember { TemplateReplaceMeJvmMainScreenEventHandler() }
        val appState by processViewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            processViewModel.initialize()
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = "TemplateReplaceMe",
            state = rememberWindowState(
                size = DpSize(600.dp, 800.dp)
            )
        ) {
            KoinScope<String>("root-window") {
                TemplateReplaceMeWindowScreen(
                    eventHandler = eventHandler,
                )
            }
        }

        if (appState.showDebugWindow) {
            Window(
                onCloseRequest = { processViewModel.setShowDebugWindow(false) },
                title = "Debug Window - TemplateReplaceMe",
                state = rememberWindowState(
                    size = DpSize(400.dp, 600.dp)
                )
            ) {
                KoinScope<String>("debug-window") {
                    TemplateReplaceMeWindowScreen(
                        eventHandler = eventHandler,
                    )
                }
            }
        }
    }
}
