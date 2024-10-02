package com.cramsan.edifikana.client.lib.koin

import androidx.room.Room
import com.cramsan.edifikana.client.lib.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val ExtrasPlatformModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "offline-db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    single { androidContext().contentResolver }
}
