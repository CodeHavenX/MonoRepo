package com.codehavenx.alpaca.frontend.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        /*
        AlpacaApplicationScreen(
            eventHandler = object : PlatformEventHandler {},
        )
         */
    }
}