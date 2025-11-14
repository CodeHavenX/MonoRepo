package com.cramsan.edifikana.client.lib.features.window

import androidx.compose.runtime.Composable
import com.cramsan.architecture.client.di.moduleList
import com.cramsan.edifikana.client.lib.di.ApplicationModule
import com.cramsan.edifikana.client.lib.di.CacheModule
import com.cramsan.edifikana.client.lib.di.ManagerModule
import com.cramsan.edifikana.client.lib.di.ManagerPlatformModule
import com.cramsan.edifikana.client.lib.di.ServiceModule
import com.cramsan.edifikana.client.lib.di.ServicePlatformModule
import com.cramsan.edifikana.client.lib.di.ViewModelModule
import com.cramsan.edifikana.client.lib.di.ViewModelPlatformModule
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
                    cacheModule = CacheModule,
                    applicationModule = ApplicationModule,
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
