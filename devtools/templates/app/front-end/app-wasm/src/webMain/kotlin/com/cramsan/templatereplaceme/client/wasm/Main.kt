package com.cramsan.templatereplaceme.client.wasm

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.cramsan.templatereplaceme.client.lib.app.TemplateReplaceMeApplicationViewModel
import com.cramsan.templatereplaceme.client.lib.app.TemplateReplaceMeWasmMainScreenEventHandler
import com.cramsan.templatereplaceme.client.lib.features.window.ComposableKoinContext
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowScreen
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.scope.KoinScope

/**
 * Main entry point for the application.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Capture the hash before entering composition. Calling handleDeepLink() inside a
    // LaunchedEffect within KoinScope would fire before ObserveViewModelEvents' collector
    // subscribes; with MutableSharedFlow(replay=0) the navigation event would be silently
    // dropped. Passing it as a parameter and firing from inside WindowsContent (after
    // ObserveViewModelEvents) guarantees the collector is active first.
    // TODO: Filter by your app's custom URI scheme before passing to handleDeepLink.
    // TODO: If using Supabase Auth, the SDK may auto-process the hash during init —
    //       verify before adding an explicit session call here.
    val initialDeepLink = window.location.hash.takeIf { it.isNotEmpty() }

    onWasmReady {
        ComposeViewport {
            ComposableKoinContext {
                val processViewModel: TemplateReplaceMeApplicationViewModel = koinViewModel()
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
