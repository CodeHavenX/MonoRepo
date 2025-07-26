package com.cramsan.sample.frontend.architecture.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.cramsan.sample.frontend.architecture.shared.App

fun main() = application {
    val windowState = rememberWindowState()

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Notes App - Desktop"
    ) {
        App()
    }
}