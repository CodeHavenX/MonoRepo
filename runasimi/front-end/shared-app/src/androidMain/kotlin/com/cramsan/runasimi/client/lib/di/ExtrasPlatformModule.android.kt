package com.cramsan.runasimi.client.lib.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val ExtrasPlatformModule = module {

    single { androidContext().contentResolver }
}
