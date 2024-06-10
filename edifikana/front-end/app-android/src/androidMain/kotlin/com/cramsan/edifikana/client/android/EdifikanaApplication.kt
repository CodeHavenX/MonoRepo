package com.cramsan.edifikana.client.android

import android.app.Application
import com.cramsan.edifikana.client.android.di.koin.ExtrasPlatformModule
import com.cramsan.edifikana.client.android.di.koin.FrameworkPlatformDelegatesModule
import com.cramsan.edifikana.client.android.di.koin.ManagerPlatformModule
import com.cramsan.edifikana.client.android.di.koin.ViewModelPlatformModule
import com.cramsan.edifikana.client.lib.di.koin.ExtrasModule
import com.cramsan.edifikana.client.lib.di.koin.FrameworkModule
import com.cramsan.edifikana.client.lib.di.koin.ManagerModule
import com.cramsan.edifikana.client.lib.di.koin.ViewModelModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named

class EdifikanaApplication : Application() {

    private val test: String by inject(named("FirebaseStorageBucketName"))

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@EdifikanaApplication)
            // Load modules
            modules(
                FrameworkModule,
                FrameworkPlatformDelegatesModule,
                ExtrasModule,
                ExtrasPlatformModule,
                ManagerModule,
                ManagerPlatformModule,
                ViewModelModule,
                ViewModelPlatformModule,
            )
        }
    }
}
