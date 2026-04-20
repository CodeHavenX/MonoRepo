package com.cramsan.flyerboard.client.android

import android.app.Application
import com.cramsan.flyerboard.client.lib.di.startAndroidApplication
import com.cramsan.flyerboard.client.lib.features.application.FlyerBoardApplicationViewModel
import org.koin.android.ext.android.inject

/**
 * Main application class for FlyerBoard Android application.
 */
class FlyerBoardApplication : Application() {

    private val processViewModel: FlyerBoardApplicationViewModel by inject()

    override fun onCreate() {
        super.onCreate()

        startAndroidApplication(this)

        processViewModel.initialize()
    }
}
