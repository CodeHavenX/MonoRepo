package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.templatereplaceme.server.controller.HealthCheckController
import com.cramsan.templatereplaceme.server.controller.UserController
import com.cramsan.templatereplaceme.server.controller.authentication.ContextRetrieverImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize and bind the ktor components.
 */
val KtorModule = module {
    // Controllers
    singleOf(::UserController)
    singleOf(::HealthCheckController)

    singleOf(::ContextRetrieverImpl) {
        bind<ContextRetriever<Unit>>()
    }

    registerControllers()
}

/**
 * Registers all controllers in a single list.
 */
fun Module.registerControllers() {
    // When adding a new controller, remember to add it to the list below
    single {
        val userController: UserController by inject()
        val healthCheckController: HealthCheckController by inject()

        listOf(
            userController,
            healthCheckController,
        )
    }
}
