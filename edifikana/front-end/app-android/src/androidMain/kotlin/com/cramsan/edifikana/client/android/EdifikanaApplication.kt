package com.cramsan.edifikana.client.android

import android.app.Application
import com.cramsan.edifikana.client.android.di.koin.AndroidModule
import com.cramsan.edifikana.client.android.di.koin.FrameworkModule
import com.cramsan.edifikana.client.android.di.koin.ManagerModule
import com.cramsan.edifikana.client.android.di.koin.ViewModelModule
import com.cramsan.framework.logging.logE
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named

class EdifikanaApplication : Application() {

    private val test: String by inject(named("FirebaseStorageBucketName"))

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger(Level.DEBUG)
            // Reference Android context
            androidContext(this@EdifikanaApplication)
            // Load modules
            modules(
                FrameworkModule,
                ManagerModule,
                AndroidModule,
                ViewModelModule,
            )
        }

        logE("TAG", test)
    }
}
