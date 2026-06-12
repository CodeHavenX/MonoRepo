package com.cramsan.flyerboard.client.wasm

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.cramsan.flyerboard.client.lib.features.application.FlyerBoardApplicationViewModel
import com.cramsan.flyerboard.client.lib.features.application.FlyerBoardWasmMainScreenEventHandler
import com.cramsan.flyerboard.client.lib.features.window.ComposableKoinContext
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowScreen
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.compose.koinInject
import org.koin.compose.scope.KoinScope

/**
 * Main entry point for the application.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        ComposeViewport {
            ComposableKoinContext {
                val processViewModel: FlyerBoardApplicationViewModel = koinInject()
                val eventHandler = remember { FlyerBoardWasmMainScreenEventHandler() }

                LaunchedEffect(Unit) {
                    processViewModel.initialize()
                }

                KoinScope<String>("root-window") {
                    FlyerBoardWindowScreen(
                        eventHandler = eventHandler,
                    )
                }
            }
        }
    }
}
