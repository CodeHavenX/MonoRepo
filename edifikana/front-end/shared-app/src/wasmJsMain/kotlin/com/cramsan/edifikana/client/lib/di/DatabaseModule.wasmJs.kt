package com.cramsan.edifikana.client.lib.di

import androidx.room.Room
import androidx.sqlite.driver.webworker.WebWorkerSQLiteDriver
import com.cramsan.edifikana.client.lib.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

@Suppress("InjectDispatcher")
internal actual val DatabaseModule = module {
    single {
        Room.databaseBuilder<AppDatabase>("appdb.db")
            .setDriver(WebWorkerSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.Default)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}
