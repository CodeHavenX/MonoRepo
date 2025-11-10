package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.architecture.server.dependencyinjection.NamedDependency
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.templatereplaceme.lib.serialization.createJson
import com.cramsan.templatereplaceme.server.controller.authentication.ContextRetrieverImpl
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

/**
 * Class to initialize all the application level components.
 */
@OptIn(ExperimentalTime::class)
val ApplicationModule = module {

    single<String>(named(NamedDependency.DOMAIN_KEY)) { "TEMPLATE_REPLACE_ME" }

    single<Json> {
        createJson()
    }

    singleOf(::ContextRetrieverImpl) {
        bind<ContextRetriever<Unit>>()
    }
}
