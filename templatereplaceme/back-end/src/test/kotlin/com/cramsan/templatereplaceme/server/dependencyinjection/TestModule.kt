package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.templatereplaceme.server.controller.UserController
import com.cramsan.templatereplaceme.server.service.UserService
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun testApplicationModule(json: Json) = module {
    single<Json> { json }

    single<ContextRetriever<*>> { mockk() }

    // Add additional test bindings if necessary
}

/**
 * Produce a Ktor module for testing.
 */
val TestControllerModule = module {
    singleOf(::UserController) {
        bind<Controller>()
    }
}

val TestServiceModule = module {
    single<UserService> { mockk() }
}
