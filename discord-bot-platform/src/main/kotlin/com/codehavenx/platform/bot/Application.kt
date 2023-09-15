package com.codehavenx.platform.bot

import com.codehavenx.platform.bot.controller.KordController
import com.codehavenx.platform.bot.controller.WebhookController
import com.codehavenx.platform.bot.di.ApplicationModule
import com.codehavenx.platform.bot.di.FrameworkModule
import com.codehavenx.platform.bot.di.createKtorModule
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.logI
import com.cramsan.framework.thread.ThreadUtilInterface
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() = launch {
    initializeDependencies()
    configureEngine()

    val webhookController: WebhookController by inject()
    val kordController: KordController by inject()
    configureRoutes(webhookController, kordController)

    startApplication()
}

fun Application.configureEngine() {
    val json: Json by inject()

    install(CallLogging)
    install(WebSockets)
    install(ContentNegotiation) {
        json(json)
    }
}

suspend fun Application.configureRoutes(
    webhookController: WebhookController,
    kordController: KordController,
) {
    kordController.start()

    routing {
        route("webhook") {
            route("github") {
                post("push") {
                    webhookController.handleGithubPushPayload(call)
                }
                post("wfjobs") {
                    webhookController.handleGithubWorkflowJobsPayload(call)
                }
            }
        }
    }
}

fun Application.initializeDependencies() {
    startKoin {
        modules(
            createKtorModule(this@initializeDependencies),
            FrameworkModule,
            ApplicationModule,
        )
    }
    val eventLogger: EventLoggerInterface by inject()
    val assertUtil: AssertUtilInterface by inject()
    val threadUtil: ThreadUtilInterface by inject()

    assertUtil.assertNotNull(eventLogger, TAG, "EventLogger is null")
    assertUtil.assertNotNull(threadUtil, TAG, "ThreadUtil is null")
}

fun Application.startApplication() {
    logI(TAG, "Application is ready.")
}

private const val TAG = "Application"
