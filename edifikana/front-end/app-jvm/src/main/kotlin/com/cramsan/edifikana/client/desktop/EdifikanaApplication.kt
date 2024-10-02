package com.cramsan.edifikana.client.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.cramsan.edifikana.client.lib.di.koin.ExtrasModule
import com.cramsan.edifikana.client.lib.di.koin.FrameworkModule
import com.cramsan.edifikana.client.lib.di.koin.ManagerModule
import com.cramsan.edifikana.client.lib.di.koin.ViewModelModule
import com.cramsan.edifikana.client.lib.features.application.EdifikanaJvmMainScreenEventHandler
import com.cramsan.edifikana.client.lib.features.main.EdifikanaApplicationScreen
import com.cramsan.edifikana.client.lib.koin.ExtrasPlatformModule
import com.cramsan.edifikana.client.lib.koin.FrameworkPlatformDelegatesModule
import com.cramsan.edifikana.client.lib.koin.ManagerPlatformModule
import com.cramsan.edifikana.client.lib.koin.ViewModelPlatformModule
import org.koin.core.context.startKoin

/**
 * Main function for the desktop application.
 */
fun main() = application {
    startKoin {
        // Load modules
        modules(
            FrameworkModule,
            FrameworkPlatformDelegatesModule,
            ExtrasModule,
            ExtrasPlatformModule,
            ManagerModule,
            ManagerPlatformModule,
            ViewModelModule,
            ViewModelPlatformModule,
        )
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Edifikana",
    ) {
        EdifikanaApplicationScreen(
            eventHandler = EdifikanaJvmMainScreenEventHandler(),
        )
    }
}
