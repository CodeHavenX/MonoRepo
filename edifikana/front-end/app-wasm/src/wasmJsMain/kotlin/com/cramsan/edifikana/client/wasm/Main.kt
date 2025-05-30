package com.cramsan.edifikana.client.wasm

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.cramsan.edifikana.client.lib.di.koin.windowModuleList
import com.cramsan.edifikana.client.lib.features.ComposableKoinContext
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.EdifikanaWindowScreen
import com.cramsan.edifikana.client.lib.features.application.EdifikanaWasmMainScreenEventHandler
import org.koin.compose.koinInject
import org.koin.compose.module.rememberKoinModules

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

            rememberKoinModules { windowModuleList }

            EdifikanaWindowScreen(
                eventHandler = eventHandler,
            )
        }
    }
}
