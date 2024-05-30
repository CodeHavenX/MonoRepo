package com.cramsan.edifikana.client.android.managers

import android.content.Context
import com.cramsan.edifikana.client.android.di.BackgroundDispatcher
import com.cramsan.edifikana.client.android.di.FirebaseStorageBucketName
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock
import javax.inject.Inject

data class WorkContext @Inject constructor(
    val clock: Clock,
    val appScope: CoroutineScope,
    @BackgroundDispatcher
    val backgroundDispatcher: CoroutineDispatcher,
    @ApplicationContext
    val appContext: Context,
    val coroutineExceptionHandler: CoroutineExceptionHandler,
    @FirebaseStorageBucketName
    val storageBucket: String,
)
