package com.cramsan.edifikana.client.desktop.koin

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.cramsan.edifikana.client.lib.db.AppDatabase
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
