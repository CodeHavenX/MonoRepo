package com.cramsan.templatereplaceme.client.lib.features.window

import androidx.compose.runtime.Composable
import com.cramsan.templatereplaceme.client.lib.di.ApplicationViewModelModule
import com.cramsan.templatereplaceme.client.lib.di.ManagerModule
import com.cramsan.templatereplaceme.client.lib.di.ManagerPlatformModule
import com.cramsan.templatereplaceme.client.lib.di.ServiceModule
import com.cramsan.templatereplaceme.client.lib.di.ServicePlatformModule
import com.cramsan.templatereplaceme.client.lib.di.ViewModelModule
import com.cramsan.templatereplaceme.client.lib.di.ViewModelPlatformModule
import com.cramsan.templatereplaceme.client.lib.di.moduleList
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
            modules(
                moduleList(
                    applicationViewModelModule = ApplicationViewModelModule,
                    serviceModule = ServiceModule,
                    servicePlatformModule = ServicePlatformModule,
                    managerModule = ManagerModule,
                    managerPlatformModule = ManagerPlatformModule,
                    viewModelModule = ViewModelModule,
                    viewModelPlatformModule = ViewModelPlatformModule,
                )
            )
        }
    ) {
        content()
    }
}
