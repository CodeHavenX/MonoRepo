package com.codehavenx.platform.bot

import com.codehavenx.platform.bot.controller.kord.DiscordController
import com.codehavenx.platform.bot.controller.webhook.WebhookController
import com.codehavenx.platform.bot.di.ApplicationModule
import com.codehavenx.platform.bot.di.FrameworkModule
import com.codehavenx.platform.bot.di.createKtorModule
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.ServerReady
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() = runBlocking {
    initializeDependencies()
    startServer()
}

fun Application.startServer() = runBlocking {
    initializeMonitoring()
    configureKtorEngine()

    val webhookController: WebhookController by inject()
    val discordController: DiscordController by inject()

    configureEntryPoints(webhookController, discordController)

    startApplication()
}

/**
 * Configures the Ktor engine.
 */
fun Application.configureKtorEngine() {
    val json: Json by inject()

    install(CallLogging)
    install(WebSockets)
    install(ContentNegotiation) {
        json(json)
    }
}

/**
 * Configures all the system entry points. This includes REST routes and Discord intents.
 */
suspend fun Application.configureEntryPoints(
    webhookController: WebhookController,
    discordController: DiscordController,
) {
    discordController.start()

    routing {
        route("webhook") {
            webhookController.registerRoutes(this@route)
        }
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
 * Initialize monitoring
 */
fun Application.initializeMonitoring() {
    val handler: (Any) -> Unit = {
        logW(TAG, it.toString())
    }
    monitor.subscribe(ApplicationStarting, handler)
    monitor.subscribe(ApplicationStarted, handler)
    monitor.subscribe(ServerReady, handler)
    monitor.subscribe(ApplicationStopPreparing, handler)
    monitor.subscribe(ApplicationStopping, handler)
    monitor.subscribe(ApplicationStopped, handler)
}

/**
 * Let's do it!
 */
fun Application.startApplication() {
    logI(TAG, "Application is ready.")
}

private const val TAG = "Application"
