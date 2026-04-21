package com.cramsan.flyerboard.client.lib.features.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cramsan.flyerboard.client.lib.features.application.FlyerBoardApplicationMainScreenEventHandler
import com.cramsan.flyerboard.client.lib.features.window.ComposableKoinContext
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowScreen
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Main activity for the application.
 */
class MainActivity : ComponentActivity(), FlyerBoardApplicationMainScreenEventHandler {

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposableKoinContext {
                KoinScope<String>("root-window") {
                    FlyerBoardWindowScreen(
                        eventHandler = this,
                    )
                }
            }
        }
    }

    override fun shareContent(event: FlyerBoardWindowsEvent.ShareContent) = Unit
}
