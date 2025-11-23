package com.cramsan.edifikana.client.lib.di

import androidx.room.Room
import com.cramsan.edifikana.client.lib.db.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

internal actual val DatabaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "database-name"
        ).build()
    }
}