package com.cramsan.samples.service.ktor

import com.cramsan.framework.logging.logI
import com.cramsan.samples.service.ktor.di.ApplicationModule
import com.cramsan.samples.service.ktor.di.FrameworkModule
import com.cramsan.samples.service.ktor.di.createKtorModule
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.head
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
    configureEntryPoints()
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
fun Application.configureEntryPoints() {
    routing {
        get("/") {
            call.respondText("Hello, World!")
        }
        head("/") {
            call.respond(HttpStatusCode.OK, "OK")
        }
    }
}

/**
 * Let's do it!
 */
fun Application.startApplication() {
    logI(TAG, "Application is ready.")
}

private const val TAG = "Application"
