package com.cramsan.tokenmanager.jvm

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.cramsan.tokenmanager.lib.TokenManager

/**
 *
 */
fun main() = application {
    Window(
        title = "Token Manager",
        onCloseRequest = ::exitApplication,
    ) {
        TokenManager()
    }
}
