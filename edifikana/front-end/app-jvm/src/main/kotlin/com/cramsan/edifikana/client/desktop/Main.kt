package com.cramsan.edifikana.client.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.cramsan.edifikana.client.lib.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "KotlinProject") {
        App()
    }
}

@Preview
@Composable
fun Test() {
    Text("Hello World")
}
