package com.cramsan.templatereplaceme.client.android

import android.app.Application
import com.cramsan.templatereplaceme.client.lib.di.startAndroidApplication
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeApplicationViewModel
import org.koin.android.ext.android.inject

/**
 * Main application class for TemplateReplaceMe Android application.
 */
class TemplateReplaceMeApplication : Application() {

    private val processViewModel: TemplateReplaceMeApplicationViewModel by inject()

    override fun onCreate() {
        super.onCreate()

        startAndroidApplication(this)

        processViewModel.initialize()
    }
}
