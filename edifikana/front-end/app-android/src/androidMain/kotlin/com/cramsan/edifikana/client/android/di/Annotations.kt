package com.cramsan.edifikana.client.android.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackgroundDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UIThreadDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseStorageBucketName
