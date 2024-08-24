package com.codehavenx.alpaca.frontend.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.codehavenx.alpaca.frontend.appcore.di.ExtrasModule
import com.codehavenx.alpaca.frontend.appcore.di.FrameworkModule
import com.codehavenx.alpaca.frontend.appcore.di.ManagerModule
import com.codehavenx.alpaca.frontend.appcore.di.ViewModelModule
import com.codehavenx.alpaca.frontend.appcore.features.application.AlpacaApplicationScreen
import com.codehavenx.alpaca.frontend.appcore.features.application.PlatformEventHandler
import org.koin.core.context.GlobalContext.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        // Load modules
        modules(
            FrameworkModule,
            ExtrasModule,
            ManagerModule,
            ViewModelModule,
        )
    }
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        AlpacaApplicationScreen(
            eventHandler = object : PlatformEventHandler {},
        )
    }
}