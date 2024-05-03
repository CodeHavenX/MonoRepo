package com.cramsan.edifikana.client.android

import android.content.Context
import coil.ImageLoader
import com.cramsan.edifikana.client.android.utils.coil.FirebaseFetcherBuilder
import com.cramsan.edifikana.client.android.utils.coil.FirebaseImageLoader
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock

@Module
@InstallIn(SingletonComponent::class)
object Dependencies {

    @Provides
    fun provideFireStore(): FirebaseFirestore {
        val settings = firestoreSettings {
            // Use persistent disk cache (default)
            setLocalCacheSettings(persistentCacheSettings {

            })
        }
        Firebase.firestore.firestoreSettings = settings
        return  Firebase.firestore
    }

    @Provides
    fun provideStorage(): FirebaseStorage {
        return  Firebase.storage
    }

    @Provides
    fun provideAuth(): FirebaseAuth {
        return  Firebase.auth
    }

    @Provides
    fun provideClock(): Clock {
        return Clock.System
    }

    @BackgroundDispatcher
    @Provides
    fun provideBackgroundDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @UIThreadDispatcher
    @Provides
    fun provideUIDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }

    @Provides
    fun provideImageLoader(
        @ApplicationContext
        context: Context,
    ): ImageLoader = ImageLoader.Builder(context)
        .components {
            // Add Firebase fetcher
            add(FirebaseFetcherBuilder())
        }
        .build()
    }
}