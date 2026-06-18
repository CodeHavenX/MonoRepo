package com.cramsan.runasimi.client.lib.di

import android.content.Context
import com.cramsan.architecture.client.di.moduleList
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Starts Koin with all the modules required for the Android application.
 *
 * @param isDebugBuild Whether the running binary is a debug build. The caller (the launcher
 * application module, which keeps a real Android Gradle Plugin variant/build-type distinction)
 * should pass its own generated BuildConfig.DEBUG here.
 */
fun startAndroidApplication(
    context: Context,
    isDebugBuild: Boolean,
) {
    startKoin {
        // Log Koin into Android logger
        androidLogger()
        // Reference Android context
        androidContext(context)
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
                platformIsDebugBuild = isDebugBuild,
            )
        )
    }
}
