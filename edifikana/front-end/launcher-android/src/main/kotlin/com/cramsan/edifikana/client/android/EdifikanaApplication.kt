package com.cramsan.edifikana.client.android

import android.app.Application
import com.cramsan.edifikana.client.lib.di.startAndroidApplication
import com.cramsan.edifikana.client.lib.features.application.EdifikanaApplicationViewModel
import org.koin.android.ext.android.inject

/**
 * Main application class for Edifikana Android application.
 */
class EdifikanaApplication : Application() {

    private val processViewModel: EdifikanaApplicationViewModel by inject()

    override fun onCreate() {
        super.onCreate()

        startAndroidApplication(this)

        processViewModel.initialize()
    }
}
