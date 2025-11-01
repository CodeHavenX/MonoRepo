package com.cramsan.edifikana.server

import com.cramsan.edifikana.server.controller.Controller
import com.cramsan.edifikana.server.dependencyinjection.ApplicationModule
import com.cramsan.edifikana.server.dependencyinjection.FrameworkModule
import com.cramsan.edifikana.server.dependencyinjection.KtorModule
import com.cramsan.edifikana.server.dependencyinjection.ServicesModule
import com.cramsan.edifikana.server.dependencyinjection.SupabaseModule
import com.cramsan.framework.core.ktor.configureHealthEndpoint
import com.cramsan.framework.logging.logI
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.ktor.ext.inject

/**
 * Main entry point of the application, used only during testing/local development.
 */
fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

/**
 * Entry point of the application.
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

    val controllerList: List<Controller> by inject()

    configureHealthEndpoint()
    configureEntryPoints(controllerList)
    startApplication()
}

/**
 * Configures the Ktor engine.
 */
fun Application.configureKtorEngine() {
    val json: Json by inject()
    val settingsHolder: SettingsHolder by inject()
    val allowedHost: String = settingsHolder.getString(PropertyKey.ALLOWED_HOST).orEmpty()

    install(CallLogging)
    install(ContentNegotiation) {
        json(json)
    }
    install(CORS) {
        // Configure the host
        allowHost(allowedHost)

        // Configure the headers that are allowed.
        // If a header is requested by the client that is not allowed, the entire
        // request will be rejected.
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("apikey")
        allowHeader("x-client-info")

        // Allow more methods. GET, POST, HEAD are allowed by default.
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
    }
}

/**
 * Initialize the service dependencies.
 */
fun Application.initializeDependencies() {
    startKoin {
        configureKoinLogging()
        modules(
            FrameworkModule,
            SupabaseModule,
            ApplicationModule,
            ServicesModule,
            KtorModule,
        )
    }
}

private fun KoinApplication.configureKoinLogging() {
    printLogger(Level.DEBUG)
}

/**
 * Configures the entry points of the application.
 */
fun Application.configureEntryPoints(
    controllerList: List<Controller>,
) {
    routing {
        controllerList.forEach { controller ->
            controller.registerRoutes(this)
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
