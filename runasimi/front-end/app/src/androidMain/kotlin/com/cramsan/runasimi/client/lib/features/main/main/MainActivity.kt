package com.cramsan.runasimi.client.lib.features.main.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cramsan.runasimi.client.lib.features.window.ComposableKoinContext
import com.cramsan.runasimi.client.lib.features.window.RunasimiMainScreenEventHandler
import com.cramsan.runasimi.client.lib.features.window.RunasimiWindowScreen
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Main activity for the application.
 */
class MainActivity : ComponentActivity(), RunasimiMainScreenEventHandler {

    @OptIn(KoinExperimentalAPI::class)
    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposableKoinContext {
                KoinScope<String>("root-window") {
                    RunasimiWindowScreen(
                        eventHandler = this,
                    )
                }
            }
        }
    }
}
