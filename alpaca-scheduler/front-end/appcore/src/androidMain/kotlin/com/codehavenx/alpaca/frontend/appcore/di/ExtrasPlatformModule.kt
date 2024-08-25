package com.codehavenx.alpaca.frontend.appcore.di

import androidx.room.Room
import com.codehavenx.alpaca.frontend.appcore.database.AppDatabase
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
