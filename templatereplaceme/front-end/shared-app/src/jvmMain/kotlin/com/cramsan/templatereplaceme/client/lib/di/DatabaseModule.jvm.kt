package com.cramsan.templatereplaceme.client.lib.di

import org.koin.dsl.module

internal actual val DatabaseModule = module {
    /*
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
     */
}
