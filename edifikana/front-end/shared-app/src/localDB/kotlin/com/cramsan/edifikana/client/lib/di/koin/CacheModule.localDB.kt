package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.db.AppDatabase
import com.cramsan.edifikana.client.lib.db.EventLogCache
import com.cramsan.edifikana.client.lib.db.EventLogRoomCache
import com.cramsan.edifikana.client.lib.db.TimeCardCache
import com.cramsan.edifikana.client.lib.db.TimeCardRoomCache
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val CacheModule = module {
    single { get<AppDatabase>().eventLogRecordDao() }

    single { get<AppDatabase>().timeCardRecordDao() }

    single { get<AppDatabase>().fileAttachmentDao() }

    singleOf(::EventLogRoomCache) {
        bind<EventLogCache>()
    }

    singleOf(::TimeCardRoomCache) {
        bind<TimeCardCache>()
    }
}
