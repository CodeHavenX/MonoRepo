package com.cramsan.edifikana.client.android

import android.app.Application
import com.cramsan.edifikana.client.lib.di.koin.moduleList
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Main application class for Edifikana Android application.
 */
class EdifikanaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@EdifikanaApplication)
            // Load modules
            modules(moduleList)
        }
    }
}
