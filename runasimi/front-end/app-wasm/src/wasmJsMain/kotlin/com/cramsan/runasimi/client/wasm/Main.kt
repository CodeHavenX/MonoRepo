package com.cramsan.runasimi.client.wasm

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.cramsan.runasimi.client.lib.features.application.RunasimiApplicationViewModel
import com.cramsan.runasimi.client.lib.features.application.RunasimiWasmMainScreenEventHandler
import com.cramsan.runasimi.client.lib.features.window.ComposableKoinContext
import com.cramsan.runasimi.client.lib.features.window.RunasimiWindowScreen
import org.koin.compose.koinInject
import org.koin.compose.scope.KoinScope

/**
 * Main entry point for the application.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport("ComposeTarget") {
        ComposableKoinContext {
            val processViewModel: RunasimiApplicationViewModel = koinInject()
            val eventHandler = remember { RunasimiWasmMainScreenEventHandler() }

            LaunchedEffect(Unit) {
                processViewModel.initialize()
            }

            KoinScope<String>("root-window") {
                RunasimiWindowScreen(
                    eventHandler = eventHandler,
                )
            }
        }
    }
}
