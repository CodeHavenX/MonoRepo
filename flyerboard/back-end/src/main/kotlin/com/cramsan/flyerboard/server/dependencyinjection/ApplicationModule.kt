package com.cramsan.flyerboard.server.dependencyinjection

import com.cramsan.architecture.server.dependencyinjection.NamedDependency
import com.cramsan.flyerboard.lib.serialization.createJson
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextRetriever
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the application level components.
 */
internal val ApplicationModule = module {

    single<String>(named(NamedDependency.DOMAIN_KEY)) { "FLYERBOARD" }

    single<Json> {
        createJson()
    }

    singleOf(::FlyerBoardContextRetriever) {
        bind<ContextRetriever<FlyerBoardContextPayload>>()
    }
}
