package com.cramsan.framework.samples.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.cramsan.framework.sample.shared.features.ApplicationScreen
import com.cramsan.framework.sample.shared.features.ComposableKoinContext
import org.koin.compose.scope.KoinScope

/**
 * Main function for the desktop application.
 */
fun main() = application {
    ComposableKoinContext {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Framework Sample",
            state = rememberWindowState(
                size = DpSize(600.dp, 800.dp),
            ),
        ) {
            KoinScope<String>("root-window") {
                ApplicationScreen()
            }
        }
    }
}
