package com.cramsan.edifikana.client.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.cramsan.edifikana.client.lib.di.koin.moduleList
import com.cramsan.edifikana.client.lib.features.application.EdifikanaJvmMainScreenEventHandler
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationScreen
import com.cramsan.edifikana.client.lib.init.Initializer
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger

/**
 * Main function for the desktop application.
 */
fun main() = application {
    startKoin {
        // Log Koin to stdout
        logger(PrintLogger())
        // Load modules
        modules(moduleList)
    }

    val initializer = Initializer()
    initializer.start()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Edifikana",
    ) {
        EdifikanaApplicationScreen(
            eventHandler = EdifikanaJvmMainScreenEventHandler(),
        )
    }
}
