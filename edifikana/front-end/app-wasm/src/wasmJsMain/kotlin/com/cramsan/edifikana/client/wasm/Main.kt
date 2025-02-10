package com.cramsan.edifikana.client.wasm

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationScreen
import com.cramsan.edifikana.client.lib.features.application.EdifikanaWasmMainScreenEventHandler

/**
 * Main entry point for the application.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        EdifikanaApplicationScreen(
            eventHandler = EdifikanaWasmMainScreenEventHandler(),
        )
    }
}
