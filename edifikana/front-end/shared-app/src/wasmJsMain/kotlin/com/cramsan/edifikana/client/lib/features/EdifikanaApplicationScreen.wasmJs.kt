package com.cramsan.edifikana.client.lib.features

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.di.koin.moduleList
import org.koin.compose.KoinApplication
import org.koin.core.logger.PrintLogger

/**
 * Edifikana main screen event handler.
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
