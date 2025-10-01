package com.cramsan.edifikana.client.android

import android.app.Application
import com.cramsan.edifikana.client.lib.di.moduleList
import com.cramsan.edifikana.client.lib.features.application.EdifikanaApplicationViewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Main application class for Edifikana Android application.
 */
class EdifikanaApplication : Application() {

    private val processViewModel: EdifikanaApplicationViewModel by inject()

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

        processViewModel.initialize()
    }
}
