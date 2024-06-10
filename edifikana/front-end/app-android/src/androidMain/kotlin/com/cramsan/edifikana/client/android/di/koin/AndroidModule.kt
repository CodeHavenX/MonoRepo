package com.cramsan.edifikana.client.android.di.koin

import androidx.room.Room
import com.cramsan.edifikana.client.android.BuildConfig
import com.cramsan.edifikana.client.lib.db.AppDatabase
import com.cramsan.framework.logging.EventLoggerInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.datetime.Clock
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val AndroidModule = module {

    single<Clock> { Clock.System }

    single {
        CoroutineExceptionHandler { _, throwable ->
            get<EventLoggerInterface>().e("CoroutineExceptionHandler", "Uncaught Exception", throwable)
        }
    }

    single { androidContext().resources }

    @OptIn(DelicateCoroutinesApi::class)
    single<CoroutineScope> { GlobalScope }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "offline-db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().eventLogRecordDao() }

    single { get<AppDatabase>().timeCardRecordDao() }

    single { get<AppDatabase>().fileAttachmentDao() }
}
