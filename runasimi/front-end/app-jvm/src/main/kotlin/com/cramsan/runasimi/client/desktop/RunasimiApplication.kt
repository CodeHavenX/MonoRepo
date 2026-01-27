package com.cramsan.runasimi.client.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.cramsan.runasimi.client.lib.features.application.RunasimiApplicationViewModel
import com.cramsan.runasimi.client.lib.features.application.RunasimiJvmMainScreenEventHandler
import com.cramsan.runasimi.client.lib.features.window.ComposableKoinContext
import com.cramsan.runasimi.client.lib.features.window.RunasimiWindowScreen
import org.koin.compose.koinInject
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Main function for the desktop application.
 */
@OptIn(KoinExperimentalAPI::class)
fun main() = application {
    ComposableKoinContext {
        val processViewModel: RunasimiApplicationViewModel = koinInject()
        val eventHandler = remember { RunasimiJvmMainScreenEventHandler() }

        LaunchedEffect(Unit) {
            processViewModel.initialize()
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = "Runasimi",
            state = rememberWindowState(
                size = DpSize(600.dp, 800.dp),
            ),
        ) {
            KoinScope<String>("root-window") {
                RunasimiWindowScreen(
                    eventHandler = eventHandler,
                )
            }
        }
    }
}
