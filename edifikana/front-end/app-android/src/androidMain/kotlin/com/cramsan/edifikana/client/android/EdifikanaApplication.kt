package com.cramsan.edifikana.client.android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.cramsan.edifikana.client.android.utils.coil.FirebaseFetcherBuilder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EdifikanaApplication : Application(), ImageLoaderFactory {

    @Inject
    lateinit var imageLoader: ImageLoader

    /**
     * Create a new instance of [ImageLoader] and configure it.
     */
    override fun newImageLoader(): ImageLoader {
        return imageLoader
    }
}