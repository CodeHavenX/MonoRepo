package com.codehavenx.alpaca.frontend.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.codehavenx.alpaca.frontend.appcore.features.application.AlpacaApplicationScreen
import com.codehavenx.alpaca.frontend.appcore.features.application.PlatformEventHandler

/**
 * Main activity for the Android app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AlpacaApplicationScreen(
                eventHandler = object : PlatformEventHandler {},
            )
        }
    }
}
