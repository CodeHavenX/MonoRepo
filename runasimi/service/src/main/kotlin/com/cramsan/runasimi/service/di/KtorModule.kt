package com.cramsan.runasimi.service.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.server.application.Application
import org.koin.dsl.module

/**
 * Class to initialize and bind the ktor components.
 */
fun createKtorModule(application: Application) = module(createdAtStart = true) {
    single { application }

    single {
        application.environment.config
    }

    single {
        HttpClient(Java) {
            engine {
            }
        }
    }
}
