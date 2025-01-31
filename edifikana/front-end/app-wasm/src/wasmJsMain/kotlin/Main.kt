package com.cramsan.edifikana.client.wasm

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.cramsan.edifikana.client.lib.di.koin.moduleList
import com.cramsan.edifikana.client.lib.features.application.EdifikanaWasmMainScreenEventHandler
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationScreen
import com.cramsan.edifikana.client.lib.init.Initializer
import com.cramsan.edifikana.client.lib.koin.NoopCacheModule
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        // Log Koin to stdout
        logger(PrintLogger())
        // Load modules
        modules(moduleList)
    }

    val initializer = Initializer()
    initializer.start()

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        EdifikanaApplicationScreen(
            eventHandler = EdifikanaWasmMainScreenEventHandler(),
        )
    }
}