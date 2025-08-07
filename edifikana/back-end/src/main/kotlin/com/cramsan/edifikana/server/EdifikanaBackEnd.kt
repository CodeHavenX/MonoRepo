package com.cramsan.edifikana.server

import com.cramsan.edifikana.server.core.controller.EventLogController
import com.cramsan.edifikana.server.core.controller.EventLogController.Companion.registerRoutes
import com.cramsan.edifikana.server.core.controller.HealthCheckController
import com.cramsan.edifikana.server.core.controller.HealthCheckController.Companion.registerRoutes
import com.cramsan.edifikana.server.core.controller.PropertyController
import com.cramsan.edifikana.server.core.controller.PropertyController.Companion.registerRoutes
import com.cramsan.edifikana.server.core.controller.StaffController
import com.cramsan.edifikana.server.core.controller.StaffController.Companion.registerRoutes
import com.cramsan.edifikana.server.core.controller.TimeCardController
import com.cramsan.edifikana.server.core.controller.TimeCardController.Companion.registerRoutes
import com.cramsan.edifikana.server.core.controller.UserController
import com.cramsan.edifikana.server.core.controller.UserController.Companion.registerRoutes
import com.cramsan.edifikana.server.di.ApplicationModule
import com.cramsan.edifikana.server.di.DummyStorageModule
import com.cramsan.edifikana.server.di.FrameworkModule
import com.cramsan.edifikana.server.di.KtorModule
import com.cramsan.edifikana.server.di.NAME_LOGGING
import com.cramsan.edifikana.server.di.ServicesModule
import com.cramsan.edifikana.server.di.SettingsModule
import com.cramsan.edifikana.server.di.SupabaseModule
import com.cramsan.edifikana.server.settings.Overrides
import com.cramsan.framework.core.ktor.configureHealthEndpoint
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.logI
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
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get
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

    val userController: UserController by inject()
    val eventLogController: EventLogController by inject()
    val propertyController: PropertyController by inject()
    val staffController: StaffController by inject()
    val timeCardController: TimeCardController by inject()
    val healthCheckController: HealthCheckController by inject()

    configureHealthEndpoint()
    configureEntryPoints(
        userController,
        eventLogController,
        propertyController,
        staffController,
        timeCardController,
        healthCheckController,
    )
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
    install(CORS) {
        // Allow the localhost origin for development purposes.
        allowHost("localhost:8080")
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
            SettingsModule,
            SupabaseModule,
            ApplicationModule,
            ServicesModule,
            KtorModule,
        )
        val disableSupabase: Boolean = get(named(Overrides.KEY_SUPABASE_DISABLE))
        if (disableSupabase) {
            loadKoinModules(DummyStorageModule)
        }
    }
}

private fun KoinApplication.configureKoinLogging() {
    val severity = Severity.fromStringOrDefault(System.getenv(NAME_LOGGING), Severity.INFO)
    when (severity) {
        Severity.VERBOSE, Severity.DEBUG -> printLogger(Level.DEBUG)
        Severity.INFO, Severity.WARNING,
        Severity.ERROR, Severity.DISABLED -> {
            // Use the default logger setting
        }
    }
}

/**
 * Configures the entry points of the application.
 */
fun Application.configureEntryPoints(
    userController: UserController,
    eventLogController: EventLogController,
    propertyController: PropertyController,
    staffController: StaffController,
    timeCardController: TimeCardController,
    healthCheckController: HealthCheckController,
) {
    routing {
        userController.registerRoutes(this@routing)
        eventLogController.registerRoutes(this@routing)
        propertyController.registerRoutes(this@routing)
        staffController.registerRoutes(this@routing)
        timeCardController.registerRoutes(this@routing)
        healthCheckController.registerRoutes(this@routing)
    }
}

/**
 * Let's do it!
 */
fun Application.startApplication() {
    logI(TAG, "Application is ready.")
}

private const val TAG = "Application"
