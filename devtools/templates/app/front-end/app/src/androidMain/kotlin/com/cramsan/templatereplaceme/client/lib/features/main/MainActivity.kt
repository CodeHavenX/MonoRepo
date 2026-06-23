package com.cramsan.templatereplaceme.client.lib.features.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import com.cramsan.templatereplaceme.client.lib.app.TemplateReplaceMeApplicationMainScreenEventHandler
import com.cramsan.templatereplaceme.client.lib.app.TemplateReplaceMeApplicationViewModel
import androidx.lifecycle.lifecycleScope
import com.cramsan.templatereplaceme.client.lib.features.window.ComposableKoinContext
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowScreen
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowViewModel
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowsEvent
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.compose.scope.KoinScope
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Main activity for the application.
 */
class MainActivity : ComponentActivity(), TemplateReplaceMeApplicationMainScreenEventHandler {

    private val windowViewModel: TemplateReplaceMeWindowViewModel by inject()

    @OptIn(KoinExperimentalAPI::class)
    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposableKoinContext {
                val processViewModel: TemplateReplaceMeApplicationViewModel = koinViewModel()
                LaunchedEffect(Unit) {
                    processViewModel.initialize()
                }
                KoinScope<String>("root-window") {
                    TemplateReplaceMeWindowScreen(
                        eventHandler = this,
                    )
                }
            }
        }
        handleIncomingIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        val uri = intent.data ?: return
        // TODO: Filter by your app's custom URI scheme before proceeding.
        // TODO: If using Supabase Auth, call supabase.handleDeeplinks(intent) here first
        //       to establish the auth session before navigating. The redirect URL configured
        //       with Supabase should embed the destination's @WebPath as its path so
        //       windowViewModel.handleDeepLink resolves it with no extra wiring.
        lifecycleScope.launch {
            windowViewModel.handleDeepLink(uri.toString())
        }
    }

    override fun shareContent(event: TemplateReplaceMeWindowsEvent.ShareContent) = Unit
}
