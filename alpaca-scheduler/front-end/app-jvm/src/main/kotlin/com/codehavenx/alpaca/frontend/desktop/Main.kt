package com.codehavenx.alpaca.frontend.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.codehavenx.alpaca.frontend.appcore.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "KotlinProject") {
        App()
    }
}
