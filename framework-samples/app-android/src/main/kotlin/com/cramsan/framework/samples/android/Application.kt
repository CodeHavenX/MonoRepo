package com.cramsan.framework.samples.android

import android.app.Application
import com.cramsan.framework.sample.shared.di.moduleList
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Main application class for the Android application.
 */
class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@Application)
            // Load modules
            modules(moduleList)
        }
    }
}
