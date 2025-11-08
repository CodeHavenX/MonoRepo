package com.cramsan.templatereplaceme.client.android

import android.app.Application
import com.cramsan.templatereplaceme.client.lib.di.moduleList
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeApplicationViewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Main application class for TemplateReplaceMe Android application.
 */
class TemplateReplaceMeApplication : Application() {

    private val processViewModel: TemplateReplaceMeApplicationViewModel by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@TemplateReplaceMeApplication)
            // Load modules
            modules(moduleList)
        }

        processViewModel.initialize()
    }
}
