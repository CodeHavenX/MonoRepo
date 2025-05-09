package com.cramsan.edifikana.client.lib.di.koin

import androidx.room.Room
import com.cramsan.edifikana.client.lib.db.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val ExtrasPlatformModule = module {

    single { androidContext().contentResolver }

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "database-name"
        ).build()
    }
}
