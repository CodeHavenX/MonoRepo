package com.cramsan.sample.frontend.jvm

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.cramsan.sample.frontend.app.TaskManagementApp

/**
 * JVM Desktop application entry point.
 * Demonstrates platform-specific app setup while reusing shared app logic.
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Task Management - Frontend Architecture Sample",
        state = WindowState(width = 1024.dp, height = 768.dp)
    ) {
        TaskManagementApp()
    }
}