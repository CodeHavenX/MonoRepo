package com.cramsan.templatereplaceme.client.lib.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific implementation of the ExtrasPlatformModule.
 * Provides Android-specific dependencies such as ContentResolver for file access.
 */
actual val ExtrasPlatformModule = module {

    single { androidContext().contentResolver }
}
