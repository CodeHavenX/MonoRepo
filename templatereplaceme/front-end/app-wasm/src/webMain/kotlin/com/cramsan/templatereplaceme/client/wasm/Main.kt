package com.cramsan.templatereplaceme.client.wasm

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeApplicationViewModel
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeWasmMainScreenEventHandler
import com.cramsan.templatereplaceme.client.lib.features.window.ComposableKoinContext
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowScreen
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.compose.koinInject
import org.koin.compose.scope.KoinScope

/**
 * Main entry point for the application.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Read the hash before entering composition so it is available as a stable parameter.
    // TODO: Filter by your app's custom scheme before passing to handleDeepLink.
    // TODO: If using Supabase Auth, the SDK auto-processes the hash during initialization.
    //       Verify before adding an explicit session call.
    val initialDeepLink = window.location.hash.takeIf { it.isNotEmpty() }

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
                        initialDeepLink = initialDeepLink,
                    )
                }
            }
        }
    }
}
