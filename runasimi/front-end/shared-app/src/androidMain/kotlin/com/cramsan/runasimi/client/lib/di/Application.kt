package com.cramsan.runasimi.client.lib.di

import android.content.Context
import com.cramsan.architecture.client.di.moduleList
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Starts Koin with all the modules required for the Android application.
 */
fun startAndroidApplication(
    context: Context,
) {
    startKoin {
        // Log Koin into Android logger
        androidLogger()
        // Reference Android context
        androidContext(context)
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
}
