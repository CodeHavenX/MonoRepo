package com.codehavenx.alpaca.frontend.appcore.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.codehavenx.alpaca.frontend.appcore.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import java.io.File

@Suppress("InjectDispatcher")
val ExtrasPlatformModule = module {

    single {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
        val builder = Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
        )

        builder
            .addMigrations()
            .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
