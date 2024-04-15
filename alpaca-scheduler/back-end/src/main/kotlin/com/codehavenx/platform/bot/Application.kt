package com.codehavenx.platform.bot

import com.codehavenx.alpaca.shared.TestShared
import com.codehavenx.platform.bot.controller.UserController
import com.codehavenx.platform.bot.di.ApplicationModule
import com.codehavenx.platform.bot.di.FrameworkModule
import com.codehavenx.platform.bot.di.createKtorModule
import com.cramsan.framework.logging.logI
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject

/**
 * Main entry point of the application, used only during testing/local development.
 */
fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

/**
 * Production entry point of the application.
 */
fun Application.module() = runBlocking {
    initializeDependencies()
    startServer()
}

/**
 * Starts the server.
 */
fun Application.startServer() = runBlocking {
    configureKtorEngine()

    val userController: UserController by inject()

    configureEntryPoints(userController)
    startApplication()
}

/**
 * Configures the Ktor engine.
 */
fun Application.configureKtorEngine() {
    val json: Json by inject()

    install(CallLogging)
    install(ContentNegotiation) {
        json(json)
    }
}

/**
 * Initialize the service dependencies.
 */
fun Application.initializeDependencies() {
    startKoin {
        modules(
            createKtorModule(this@initializeDependencies),
            FrameworkModule,
            ApplicationModule,
        )
    }
}

/**
 * Configures the entry points of the application.
 */
suspend fun Application.configureEntryPoints(
    userController: UserController,
) {
    routing {
        userController.registerRoutes(this@routing)
    }
}

/**
 * Let's do it!
 */
fun Application.startApplication() {
    logI(TAG, "Application is ready.")
    logI(TAG, "Application is ready. + ${TestShared.TEST}")
}

private const val TAG = "Application"
