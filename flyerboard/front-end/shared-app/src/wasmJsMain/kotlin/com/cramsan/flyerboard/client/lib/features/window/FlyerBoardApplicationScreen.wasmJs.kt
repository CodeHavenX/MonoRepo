package com.cramsan.flyerboard.client.lib.features.window

import androidx.compose.runtime.Composable
import com.cramsan.architecture.client.di.moduleList
import com.cramsan.flyerboard.client.lib.di.ApplicationModule
import com.cramsan.flyerboard.client.lib.di.CacheModule
import com.cramsan.flyerboard.client.lib.di.DatabaseModule
import com.cramsan.flyerboard.client.lib.di.ManagerModule
import com.cramsan.flyerboard.client.lib.di.ManagerPlatformModule
import com.cramsan.flyerboard.client.lib.di.ServiceModule
import com.cramsan.flyerboard.client.lib.di.ServicePlatformModule
import com.cramsan.flyerboard.client.lib.di.ViewModelModule
import com.cramsan.flyerboard.client.lib.di.ViewModelPlatformModule
import org.koin.compose.KoinApplication
import org.koin.core.logger.PrintLogger

/**
 * FlyerBoard main screen event handler.
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
                    databaseModule = DatabaseModule,
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
