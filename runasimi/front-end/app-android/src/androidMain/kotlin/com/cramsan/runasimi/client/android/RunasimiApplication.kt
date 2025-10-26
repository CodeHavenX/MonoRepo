package com.cramsan.runasimi.client.android

import android.app.Application
import com.cramsan.runasimi.client.lib.di.moduleList
import com.cramsan.runasimi.client.lib.features.application.RunasimiApplicationViewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Main application class for Runasimi Android application.
 */
class RunasimiApplication : Application() {

    private val processViewModel: RunasimiApplicationViewModel by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@RunasimiApplication)
            // Load modules
            modules(moduleList)
        }

        processViewModel.initialize()
    }
}
