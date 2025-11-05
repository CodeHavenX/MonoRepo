package com.cramsan.framework.samples.wasm

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.cramsan.framework.sample.shared.features.ApplicationScreen
import org.jetbrains.skiko.wasm.onWasmReady

/**
 * Main entry point for the application.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        ComposeViewport {
            ApplicationScreen()
        }
    }
}
