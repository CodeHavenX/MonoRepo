package com.cramsan.templatereplaceme.server

import com.cramsan.architecture.server.startBackEndApplication
import com.cramsan.architecture.server.startKtor
import com.cramsan.templatereplaceme.server.dependencyinjection.ApplicationModule
import com.cramsan.templatereplaceme.server.dependencyinjection.ControllerModule
import com.cramsan.templatereplaceme.server.dependencyinjection.DatastoreModule
import com.cramsan.templatereplaceme.server.dependencyinjection.ServicesModule
import io.ktor.server.application.Application

/**
 * Main entry point of the application, used only during testing/local development.
 */
fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

/**
 * Entry point of the application.
 */
fun Application.module() = startBackEndApplication(
    applicationModule = ApplicationModule,
    controllerModule = ControllerModule,
    serviceModule = ServicesModule,
    dataStoreModule = DatastoreModule,
)

/**
 * Starts the ktor component directly and skipping the configuring dependencies.
 */
fun Application.startServer() = startKtor()
