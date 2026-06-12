package com.cramsan.runasimi.client.android

import android.app.Application
import com.cramsan.runasimi.client.lib.di.startAndroidApplication
import com.cramsan.runasimi.client.lib.features.application.RunasimiApplicationViewModel
import org.koin.android.ext.android.inject

/**
 * Main application class for Runasimi Android application.
 */
class RunasimiApplication : Application() {

    private val processViewModel: RunasimiApplicationViewModel by inject()

    override fun onCreate() {
        super.onCreate()

        startAndroidApplication(this)

        processViewModel.initialize()
    }
}
