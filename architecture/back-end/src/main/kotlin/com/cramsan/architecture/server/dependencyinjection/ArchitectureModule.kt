package com.cramsan.architecture.server.dependencyinjection

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.framework.utils.time.Chronos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val ArchitectureModule = module(createdAtStart = true) {

    @OptIn(DelicateCoroutinesApi::class)
    single<CoroutineScope> {
        GlobalScope
    }

    single<Clock> {
        Chronos.initializeClock(clock = Clock.System)
        Chronos.clock()
    }

    // Default to empty stage key. This key will be used for use-cases when we need to have separation based on
    // stages defined at compiled time. The main use of this is to separate integration test configurations.
    single<String>(named(NamedDependency.STAGE_KEY)) { "" }

    single {
        SettingsHolder(get())
    }
}
