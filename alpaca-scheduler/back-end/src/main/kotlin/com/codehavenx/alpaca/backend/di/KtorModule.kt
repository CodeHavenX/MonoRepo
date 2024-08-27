package com.codehavenx.alpaca.backend.di

import com.codehavenx.alpaca.backend.core.controller.HealthCheckController
import com.codehavenx.alpaca.backend.core.controller.UserController
import io.ktor.server.application.Application
import org.koin.dsl.module

/**
 * Class to initialize and bind the ktor components.
 */
fun createKtorModule(application: Application) = module {
    single { application }

    single {
        application.environment.config
    }

    single<UserController> {
        UserController(get())
    }

    single<HealthCheckController> {
        HealthCheckController()
    }
}
