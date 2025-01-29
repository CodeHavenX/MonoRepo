package com.cramsan.edifikana.client.wasm

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.cramsan.edifikana.client.lib.di.koin.ExtrasModule
import com.cramsan.edifikana.client.lib.di.koin.FrameworkModule
import com.cramsan.edifikana.client.lib.di.koin.ManagerModule
import com.cramsan.edifikana.client.lib.di.koin.SettingsModule
import com.cramsan.edifikana.client.lib.di.koin.SupabaseModule
import com.cramsan.edifikana.client.lib.di.koin.SupabaseOverridesModule
import com.cramsan.edifikana.client.lib.di.koin.ViewModelModule
import com.cramsan.edifikana.client.lib.features.application.EdifikanaWasmMainScreenEventHandler
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationScreen
import com.cramsan.edifikana.client.lib.init.Initializer
import com.cramsan.edifikana.client.lib.koin.ExtrasPlatformModule
import com.cramsan.edifikana.client.lib.koin.FrameworkPlatformDelegatesModule
import com.cramsan.edifikana.client.lib.koin.ManagerPlatformModule
import com.cramsan.edifikana.client.lib.koin.NoopCacheModule
import com.cramsan.edifikana.client.lib.koin.ViewModelPlatformModule
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        // Load modules
        modules(
            FrameworkModule,
            FrameworkPlatformDelegatesModule,
            SettingsModule,
            ExtrasModule,
            ExtrasPlatformModule,
            ManagerModule,
            ManagerPlatformModule,
            NoopCacheModule,
            SupabaseModule,
            SupabaseOverridesModule,
            ViewModelModule,
            ViewModelPlatformModule,
        )
    }

    val initializer = Initializer()
    initializer.start()

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        EdifikanaApplicationScreen(
            eventHandler = EdifikanaWasmMainScreenEventHandler(),
        )
    }
}