package com.cramsan.edifikana.client.lib.ui.di

import coil3.Image
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.coil.Coil3Integration
import io.github.jan.supabase.storage.StorageItem

/**
 * A dummy implementation of the Coil3Integration that provides a dummy fetcher that always returns a dummy image.
 */
class DummyCoil3Integration : Coil3Integration {
    override val config: Coil3Integration.Config = Coil3Integration.Config()

    override val supabaseClient: SupabaseClient
        get() = TODO()

    override fun create(data: StorageItem, options: Options, imageLoader: ImageLoader): Fetcher {
        return DummyCoil3Fetcher()
    }
}

@Suppress("MagicNumber")
private object DummyImage : Image {
    override val size: Long = 1024
    override val width: Int = 100
    override val height: Int = 100
    override val shareable: Boolean = true
    override fun draw(canvas: coil3.Canvas) = Unit
}

private class DummyCoil3Fetcher : Fetcher {
    override suspend fun fetch(): FetchResult? {
        return ImageFetchResult(
            image = DummyImage,
            isSampled = false,
            dataSource = DataSource.NETWORK,
        )
    }
}
