package com.cramsan.templatereplaceme.client.wasm

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeApplicationViewModel
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeWasmMainScreenEventHandler
import com.cramsan.templatereplaceme.client.lib.features.window.ComposableKoinContext
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowScreen
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
                val processViewModel: TemplateReplaceMeApplicationViewModel = koinInject()
                val eventHandler = remember { TemplateReplaceMeWasmMainScreenEventHandler() }

                LaunchedEffect(Unit) {
                    processViewModel.initialize()
                }

                KoinScope<String>("root-window") {
                    TemplateReplaceMeWindowScreen(
                        eventHandler = eventHandler,
                    )
                }
            }
        }
    }
}
