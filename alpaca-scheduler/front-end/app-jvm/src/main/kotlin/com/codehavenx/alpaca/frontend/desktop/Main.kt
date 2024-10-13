package com.codehavenx.alpaca.frontend.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.codehavenx.alpaca.frontend.appcore.di.ExtrasModule
import com.codehavenx.alpaca.frontend.appcore.di.ExtrasPlatformModule
import com.codehavenx.alpaca.frontend.appcore.di.FrameworkModule
import com.codehavenx.alpaca.frontend.appcore.di.FrameworkPlatformDelegatesModule
import com.codehavenx.alpaca.frontend.appcore.di.ManagerModule
import com.codehavenx.alpaca.frontend.appcore.di.ViewModelModule
import com.codehavenx.alpaca.frontend.appcore.features.application.AlpacaApplicationScreen
import com.codehavenx.alpaca.frontend.appcore.features.application.PlatformEventHandler
import org.koin.core.context.startKoin

/**
 * Main function for the desktop application.
 * Do not launch this function directly, as that will skip several build steps.
 * To launch the application, use the gradle task `app-jvm:run`.
 */
fun main() = application {
    startKoin {
        // Load modules
        modules(
            FrameworkPlatformDelegatesModule,
            FrameworkModule,
            ExtrasModule,
            ExtrasPlatformModule,
            ManagerModule,
            ViewModelModule,
        )
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Alpaca Scheduler",
    ) {
        AlpacaApplicationScreen(
            eventHandler = object : PlatformEventHandler {},
        )
    }
}
