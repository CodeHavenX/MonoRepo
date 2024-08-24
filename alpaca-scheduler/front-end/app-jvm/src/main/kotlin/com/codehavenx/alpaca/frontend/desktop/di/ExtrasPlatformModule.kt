package com.codehavenx.alpaca.frontend.desktop.di

import org.koin.dsl.module

@Suppress("InjectDispatcher")
val ExtrasPlatformModule = module {

    // #66: Enable Room in appcore project
    /*
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
    */
}
