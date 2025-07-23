package com.cramsan.sample.frontend.wasm

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.cramsan.sample.frontend.app.TaskManagementApp

/**
 * WASM Web application entry point.
 * Demonstrates platform-specific app setup for web while reusing shared app logic.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(
        title = "Task Management - Frontend Architecture Sample",
        canvasElementId = "ComposeTarget"
    ) {
        TaskManagementApp()
    }
}
