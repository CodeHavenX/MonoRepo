package com.cramsan.templatereplaceme.client.lib.features.window

import androidx.compose.runtime.Composable
import com.cramsan.templatereplaceme.client.lib.di.moduleList
import org.koin.compose.KoinApplication
import org.koin.core.logger.PrintLogger

/**
 * TemplateReplaceMe main screen event handler.
 */
@Composable
actual fun ComposableKoinContext(content: @Composable () -> Unit) {
    KoinApplication(
        application = {
            // Log Koin to stdout
            logger(PrintLogger())
            // Load modules
            modules(moduleList)
        }
    ) {
        content()
    }
}
