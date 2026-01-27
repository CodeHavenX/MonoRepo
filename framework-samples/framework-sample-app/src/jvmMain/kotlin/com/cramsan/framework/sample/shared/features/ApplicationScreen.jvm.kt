package com.cramsan.framework.sample.shared.features

import androidx.compose.runtime.Composable
import com.cramsan.framework.sample.shared.di.moduleList
import org.koin.compose.KoinApplication
import org.koin.core.logger.PrintLogger

/**
 * Application content with DI context.
 */
@Composable
actual fun ComposableKoinContext(content: @Composable () -> Unit) {
    KoinApplication(
        application = {
            // Log Koin to stdout
            logger(PrintLogger())
            // Load modules
            modules(moduleList)
        },
    ) {
        content()
    }
}
