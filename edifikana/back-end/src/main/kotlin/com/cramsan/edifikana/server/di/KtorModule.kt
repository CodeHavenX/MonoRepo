package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.server.core.controller.HealthCheckController
import com.cramsan.edifikana.server.core.controller.UserController
import io.ktor.server.application.Application
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize and bind the ktor components.
 */
fun createKtorModule(application: Application) = module {
    single { application }

    single {
        application.environment.config
    }

    singleOf(::UserController)
    singleOf(::HealthCheckController)
}
