package com.cramsan.runasimi.android

import android.app.Application
import com.cramsan.framework.logging.EventLoggerInterface
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RunasimiApplication : Application() {

    @Inject
    lateinit var eventLogger: EventLoggerInterface

    override fun onCreate() {
        super.onCreate()

        eventLogger
    }
}
