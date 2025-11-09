package com.cramsan.architecture.server

import com.cramsan.architecture.server.dependencyinjection.ArchitectureModule
import com.cramsan.architecture.server.dependencyinjection.FrameworkModule
import com.cramsan.architecture.server.dependencyinjection.KtorModule
import com.cramsan.architecture.server.settings.BackEndApplicationSettingKey
import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.framework.core.ktor.Controller
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
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject
import org.koin.core.module.Module

/**
 * Entry point of the application.
 */
fun Application.startBackEndApplication(
    applicationModule: Module,
    controllerModule: Module,
    serviceModule: Module,
    dataStoreModule: Module,
    architectureModule: Module = ArchitectureModule,
    ktorModule: Module = KtorModule,
    frameworkModule: Module = FrameworkModule,
) = runBlocking {
    initializeDependencies(
        applicationModule = applicationModule,
        controllerModule = controllerModule,
        serviceModule = serviceModule,
        dataStoreModule = dataStoreModule,
        architectureModule = architectureModule,
        ktorModule = ktorModule,
        frameworkModule = frameworkModule,
    )
    startKtor()
}

/**
 * Starts the server.
 */
fun Application.startKtor() = runBlocking {
    configureKtorEngine()

    val controllerList: List<Controller> by inject()

    configureHealthEndpoint()
    configureEntryPoints(controllerList)
    startApplication()
}

/**
 * Configures the Ktor engine.
 */
private fun Application.configureKtorEngine() {
    val json: Json by inject()
    val settingsHolder: SettingsHolder by inject()
    val allowedHost: String = settingsHolder.getString(BackEndApplicationSettingKey.AllowedHost).orEmpty()

    install(CallLogging)
    install(ContentNegotiation) {
        json(json)
    }

    if (allowedHost.isNotBlank()) {
        install(CORS) {
            // Configure the host
            allowHost(allowedHost)

            // Configure the headers that are allowed.
            // If a header is requested by the client that is not allowed, the entire
            // request will be rejected.
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)

            // Allow more methods. GET, POST, HEAD are allowed by default.
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Patch)
            allowMethod(HttpMethod.Delete)
        }
    }
}

/**
 * Initialize the service dependencies.
 */
private fun initializeDependencies(
    applicationModule: Module,
    controllerModule: Module,
    serviceModule: Module,
    dataStoreModule: Module,
    architectureModule: Module,
    ktorModule: Module,
    frameworkModule: Module,
) {
    startKoin {
        modules(
            frameworkModule,
            ktorModule,
            architectureModule,
            dataStoreModule,
            serviceModule,
            controllerModule,
            applicationModule,
        )
    }
}


/**
 * Configures the entry points of the application.
 */
private fun Application.configureEntryPoints(
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
private fun startApplication() {
    logI(TAG, "BackEndApplication is ready.")
}

private const val TAG = "BackEndApplication"
