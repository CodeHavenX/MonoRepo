package com.cramsan.edifikana.client.lib.features.window

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.di.moduleList
import org.koin.compose.KoinApplication
import org.koin.core.logger.PrintLogger

/**
 * Composable Koin context.
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
