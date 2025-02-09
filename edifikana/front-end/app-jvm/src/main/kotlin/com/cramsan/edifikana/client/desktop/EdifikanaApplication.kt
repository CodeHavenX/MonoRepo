package com.cramsan.edifikana.client.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationScreen
import com.cramsan.edifikana.client.lib.features.application.EdifikanaJvmMainScreenEventHandler

/**
 * Main function for the desktop application.
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Edifikana",
    ) {
        EdifikanaApplicationScreen(
            eventHandler = EdifikanaJvmMainScreenEventHandler(),
        )
    }
}
