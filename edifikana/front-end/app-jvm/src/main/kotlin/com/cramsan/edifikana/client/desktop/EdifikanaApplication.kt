package com.cramsan.edifikana.client.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationScreen
import com.cramsan.edifikana.client.lib.features.application.EdifikanaJvmMainScreenEventHandler

/**
 * Main function for the desktop application.
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Edifikana",
        state = rememberWindowState(
            size = DpSize(600.dp, 800.dp)
        )
    ) {
        EdifikanaApplicationScreen(
            eventHandler = EdifikanaJvmMainScreenEventHandler(),
        )
    }
}
