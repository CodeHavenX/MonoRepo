package com.cramsan.edifikana.server.dependencyinjection

import com.cramsan.architecture.server.dependencyinjection.NamedDependency
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextRetriever
import com.cramsan.edifikana.server.service.password.PasswordGenerator
import com.cramsan.edifikana.server.service.password.SimplePasswordGenerator
import com.cramsan.framework.core.ktor.auth.ContextRetriever
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

    single<String>(named(NamedDependency.DOMAIN_KEY)) { "EDIFIKANA" }

    single<Json> {
        createJson()
    }

    singleOf(::SimplePasswordGenerator) {
        bind<PasswordGenerator>()
    }

    singleOf(::SupabaseContextRetriever) {
        bind<ContextRetriever<SupabaseContextPayload>>()
    }
}
