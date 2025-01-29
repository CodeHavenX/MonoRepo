package com.cramsan.edifikana.client.android

import android.app.Application
import com.cramsan.edifikana.client.lib.di.koin.ExtrasModule
import com.cramsan.edifikana.client.lib.di.koin.FrameworkModule
import com.cramsan.edifikana.client.lib.di.koin.ManagerModule
import com.cramsan.edifikana.client.lib.di.koin.SupabaseModule
import com.cramsan.edifikana.client.lib.di.koin.SupabaseOverridesModule
import com.cramsan.edifikana.client.lib.di.koin.ViewModelModule
import com.cramsan.edifikana.client.lib.init.Initializer
import com.cramsan.edifikana.client.lib.koin.CacheModule
import com.cramsan.edifikana.client.lib.koin.ExtrasPlatformModule
import com.cramsan.edifikana.client.lib.koin.FrameworkPlatformDelegatesModule
import com.cramsan.edifikana.client.lib.koin.ManagerPlatformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Main application class for Edifikana Android application.
 */
class EdifikanaApplication : Application() {

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
                CacheModule,
                SupabaseModule,
                SupabaseOverridesModule,
                ViewModelModule,
            )
        }

        val initializer = Initializer()
        initializer.start()
    }
}
