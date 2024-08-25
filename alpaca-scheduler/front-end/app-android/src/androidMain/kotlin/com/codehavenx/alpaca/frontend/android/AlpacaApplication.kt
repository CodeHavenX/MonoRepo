package com.codehavenx.alpaca.frontend.android

import android.app.Application
import com.codehavenx.alpaca.frontend.appcore.di.ExtrasModule
import com.codehavenx.alpaca.frontend.appcore.di.ExtrasPlatformModule
import com.codehavenx.alpaca.frontend.appcore.di.FrameworkModule
import com.codehavenx.alpaca.frontend.appcore.di.FrameworkPlatformDelegatesModule
import com.codehavenx.alpaca.frontend.appcore.di.ManagerModule
import com.codehavenx.alpaca.frontend.appcore.di.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Application class.
 */
class AlpacaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@AlpacaApplication)
            // Load modules
            modules(
                FrameworkPlatformDelegatesModule,
                FrameworkModule,
                ExtrasModule,
                ExtrasPlatformModule,
                ManagerModule,
                ViewModelModule,
            )
        }
    }
}
