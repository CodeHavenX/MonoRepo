package com.cramsan.flyerboard.client.desktop

import androidx.compose.runtime.LaunchedEffect
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
 * Main entry point for the FlyerBoard desktop application.
 */
@OptIn(KoinExperimentalAPI::class)
fun main() =
    application {
        ComposableKoinContext {
            val processViewModel: FlyerBoardApplicationViewModel = koinInject()
            val eventHandler = remember { FlyerBoardJvmMainScreenEventHandler() }

            LaunchedEffect(Unit) {
                processViewModel.initialize()
            }

            Window(
                onCloseRequest = ::exitApplication,
                title = "FlyerBoard",
                state = rememberWindowState(
                    size = DpSize(600.dp, 800.dp),
                ),
            ) {
                KoinScope<String>("root-window") {
                    FlyerBoardWindowScreen(
                        eventHandler = eventHandler,
                    )
                }
            }
        }
    }
