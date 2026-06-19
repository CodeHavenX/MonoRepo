package com.cramsan.templatereplaceme.client.android

import android.app.Application
import com.cramsan.templatereplaceme.client.android.BuildConfig
import com.cramsan.templatereplaceme.client.lib.di.startAndroidApplication

/**
 * Main application class for TemplateReplaceMe Android application.
 */
class TemplateReplaceMeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startAndroidApplication(this, BuildConfig.DEBUG)
    }
}
