package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.framework.utils.time.Chronos
import com.cramsan.templatereplaceme.lib.serialization.createJson
import com.cramsan.templatereplaceme.server.SettingsHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Class to initialize all the application level components.
 */
@OptIn(ExperimentalTime::class)
val ApplicationModule = module {

    @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
    single<CoroutineScope> {
        GlobalScope
    }

    single<Json> {
        createJson()
    }

    single<Clock>(createdAtStart = true) {
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
