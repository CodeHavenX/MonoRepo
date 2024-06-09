package com.cramsan.edifikana.client.android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.cramsan.edifikana.client.android.di.koin.AndroidModule
import com.cramsan.edifikana.client.android.di.koin.FrameworkModule
import com.cramsan.edifikana.client.android.di.koin.ManagerModule
import com.cramsan.edifikana.client.android.di.koin.ViewModelModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@HiltAndroidApp
class EdifikanaApplication : Application(), ImageLoaderFactory {

    private val imageLoader: ImageLoader by inject()

    /**
     * Create a new instance of [ImageLoader] and configure it.
     */
    override fun newImageLoader(): ImageLoader {
        return imageLoader
    }

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
                ManagerModule,
                AndroidModule,
                ViewModelModule,
            )
        }
    }
}
