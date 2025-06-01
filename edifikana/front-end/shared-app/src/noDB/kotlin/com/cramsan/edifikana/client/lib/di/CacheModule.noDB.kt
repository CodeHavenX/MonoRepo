package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.db.EventLogCache
import com.cramsan.edifikana.client.lib.db.EventLogNoopCache
import com.cramsan.edifikana.client.lib.db.TimeCardCache
import com.cramsan.edifikana.client.lib.db.TimeCardNoopCache
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val CacheModule = module {
    singleOf(::EventLogNoopCache) {
        bind<EventLogCache>()
    }

    singleOf(::TimeCardNoopCache) {
        bind<TimeCardCache>()
    }
}
