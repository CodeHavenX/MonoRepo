package com.cramsan.edifikana.client.wasm

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.cramsan.edifikana.client.lib.features.application.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.application.EdifikanaWasmMainScreenEventHandler
import com.cramsan.edifikana.client.lib.features.window.ComposableKoinContext
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowScreen
import org.koin.compose.koinInject
import org.koin.compose.scope.KoinScope

/**
 * Main entry point for the application.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        ComposableKoinContext {
            val processViewModel: EdifikanaApplicationViewModel = koinInject()
            val eventHandler = remember { EdifikanaWasmMainScreenEventHandler() }

            LaunchedEffect(Unit) {
                processViewModel.initialize()
            }

            KoinScope<String>("root-window") {
                EdifikanaWindowScreen(
                    eventHandler = eventHandler,
                )
            }
        }
    }
}
