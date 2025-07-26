package com.cramsan.sample.frontend.android

import android.app.Application

/**
 * Android application class.
 * Entry point for Android-specific initialization.
 */
class TaskManagementApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Android-specific initialization can be added here
        // e.g., Analytics, Crash reporting, etc.
    }
}