package com.cramsan.edifikana.client.lib.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.cramsan.edifikana.client.lib.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import java.io.File

internal actual val DatabaseModule = module {

    @Suppress("InjectDispatcher")
    single {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
        val builder = Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
        )

        builder
            .addMigrations()
            .fallbackToDestructiveMigration(dropAllTables = true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
