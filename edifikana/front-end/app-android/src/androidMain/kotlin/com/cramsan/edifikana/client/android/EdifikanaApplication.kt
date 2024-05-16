package com.cramsan.edifikana.client.android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.logging.EventLoggerInterface
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EdifikanaApplication : Application(), ImageLoaderFactory {

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var eventLogger: EventLoggerInterface

    @Inject
    lateinit var assertUtilInterface: AssertUtilInterface

    /**
     * Create a new instance of [ImageLoader] and configure it.
     */
    override fun newImageLoader(): ImageLoader {
        return imageLoader
    }

    override fun onCreate() {
        super.onCreate()

        eventLogger.i(TAG, "onCreate")
    }

    companion object {
        private const val TAG = "EdifikanaApplication"
    }
}
