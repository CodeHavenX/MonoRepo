package com.codehavenx.platform.bot

import com.codehavenx.platform.bot.controller.kord.DiscordController
import com.codehavenx.platform.bot.controller.webhook.WebhookController
import com.codehavenx.platform.bot.di.ApplicationModule
import com.codehavenx.platform.bot.di.FrameworkModule
import com.codehavenx.platform.bot.di.createKtorModule
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.logI
import com.cramsan.framework.thread.ThreadUtilInterface
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
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
    configureKtorEngine()

    val webhookController: WebhookController by inject()
    val discordController: DiscordController by inject()
    val eventLogger: EventLoggerInterface by inject()
    val assertUtil: AssertUtilInterface by inject()
    val threadUtil: ThreadUtilInterface by inject()

    assertUtil.assertNotNull(eventLogger, TAG, "EventLogger is null")
    assertUtil.assertNotNull(threadUtil, TAG, "ThreadUtil is null")

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
 * Let's do it!
 */
fun Application.startApplication() {
    logI(TAG, "Application is ready.")
}

private const val TAG = "Application"
