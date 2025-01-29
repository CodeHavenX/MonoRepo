package com.cramsan.samples.service.ktor.di

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
}
