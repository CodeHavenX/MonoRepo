package com.cramsan.flyerboard.server

import com.cramsan.architecture.server.startBackEndApplication
import com.cramsan.architecture.server.startKtor
import com.cramsan.flyerboard.server.dependencyinjection.ApplicationModule
import com.cramsan.flyerboard.server.dependencyinjection.ControllerModule
import com.cramsan.flyerboard.server.dependencyinjection.DatastoreModule
import com.cramsan.flyerboard.server.dependencyinjection.ServicesModule
import com.cramsan.flyerboard.server.service.ExpiryService
import io.ktor.server.application.Application
import org.koin.ktor.ext.inject

/**
 * Main entry point of the application, used only during testing/local development.
 */
fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

/**
 * Entry point of the application.
 */
fun Application.module() {
    startBackEndApplication(
        applicationModule = ApplicationModule,
        controllerModule = ControllerModule,
        serviceModule = ServicesModule,
        dataStoreModule = DatastoreModule,
    )
    val expiryService: ExpiryService by inject()
    expiryService.start(this)
}

/**
 * Starts the ktor component directly and skipping the configuring dependencies.
 */
fun Application.startServer() = startKtor()
