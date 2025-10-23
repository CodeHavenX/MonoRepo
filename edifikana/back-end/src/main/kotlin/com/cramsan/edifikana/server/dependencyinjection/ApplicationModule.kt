package com.cramsan.edifikana.server.dependencyinjection

import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.service.password.PasswordGenerator
import com.cramsan.edifikana.server.service.password.SimplePasswordGenerator
import com.cramsan.framework.utils.time.Chronos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
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

    singleOf(::SimplePasswordGenerator) {
        bind<PasswordGenerator>()
    }

    single<String>(named(STAGE_KEY)) {
        System.getenv("EDIFIKANA_STAGE_KEY").orEmpty()
    }
}

const val STAGE_KEY = "edifikana.stage"
