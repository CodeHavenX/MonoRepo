package com.cramsan.templatereplaceme.client.lib.features.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeApplicationMainScreenEventHandler
import com.cramsan.templatereplaceme.client.lib.features.window.ComposableKoinContext
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowScreen
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowsEvent
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Main activity for the application.
 */
class MainActivity : ComponentActivity(), TemplateReplaceMeApplicationMainScreenEventHandler {

    @OptIn(KoinExperimentalAPI::class)
    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposableKoinContext {
                KoinScope<String>("root-window") {
                    TemplateReplaceMeWindowScreen(
                        eventHandler = this,
                    )
                }
            }
        }
    }

    override fun shareContent(event: TemplateReplaceMeWindowsEvent.ShareContent) = Unit
}
