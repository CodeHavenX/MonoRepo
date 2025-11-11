package com.cramsan.templatereplaceme.client.lib.di

import android.content.Context
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
}
