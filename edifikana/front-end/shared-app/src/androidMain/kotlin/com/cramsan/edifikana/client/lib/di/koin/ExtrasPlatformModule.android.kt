package com.cramsan.edifikana.client.lib.di.koin

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val ExtrasPlatformModule = module {

    single { androidContext().contentResolver }

}
