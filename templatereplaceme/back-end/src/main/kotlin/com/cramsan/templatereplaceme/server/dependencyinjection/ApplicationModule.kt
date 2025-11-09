package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.templatereplaceme.lib.serialization.createJson
import com.cramsan.templatereplaceme.server.controller.authentication.ContextRetrieverImpl
import kotlin.time.ExperimentalTime
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize all the application level components.
 */
@OptIn(ExperimentalTime::class)
val ApplicationModule = module {

    single<Json> {
        createJson()
    }

    singleOf(::ContextRetrieverImpl) {
        bind<ContextRetriever<Unit>>()
    }
}
