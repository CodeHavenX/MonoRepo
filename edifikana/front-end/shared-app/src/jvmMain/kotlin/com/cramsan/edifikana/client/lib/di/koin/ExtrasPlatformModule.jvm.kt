package com.cramsan.edifikana.client.lib.di.koin

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.cramsan.edifikana.client.lib.db.AppDatabase
import java.io.File
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

@Suppress("InjectDispatcher")
actual val ExtrasPlatformModule = module {

    single {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
        val builder = Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
        )

        builder
            .addMigrations()
            // TODO: Remove this once we have reached production
            .fallbackToDestructiveMigration(dropAllTables = true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

}
