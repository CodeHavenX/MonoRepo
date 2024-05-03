package com.cramsan.edifikana.client.android.utils.coil

import coil.ImageLoader
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class FirebaseFetcher(
    private val firebaseStorage: FirebaseStorage,
    private val options: Options,
) : Fetcher {

    override suspend fun fetch(): FetchResult {

    }
}

class FirebaseFetcherBuilder @Inject constructor(
    val firebaseStorage: FirebaseStorage,
) : Fetcher.Factory<FirebaseStorageRef> {

    override fun create(data: FirebaseStorageRef, options: Options, imageLoader: ImageLoader): Fetcher? {
        return FirebaseFetcher(
            firebaseStorage,
            options,
        )
    }
}
