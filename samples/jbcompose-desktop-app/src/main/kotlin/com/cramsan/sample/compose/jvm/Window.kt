package com.cramsan.sample.compose.jvm

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.cramsan.sample.mpplib.compose.common.MainView

/**
 *
 */
fun main() = application {
    Window(
        title = "Sample Compose Desktop App",
        onCloseRequest = ::exitApplication,
    ) {
        MainView()
    }
}

@Preview
@Composable
private fun Preview() {
    MainView()
}
