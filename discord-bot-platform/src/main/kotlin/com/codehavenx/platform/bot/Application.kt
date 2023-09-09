package com.codehavenx.platform.bot

import com.codehavenx.platform.bot.di.ApplicationModule
import com.codehavenx.platform.bot.di.FrameworkModule
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.logI
import com.cramsan.framework.thread.ThreadUtilInterface
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import org.kohsuke.github.GitHub
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject


fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    initializeDependencies()
    configureEngine()
    configureRoutes()
    startApplication()
}

fun Application.configureEngine() {
    install(WebSockets)
}

fun Application.configureRoutes() {
    routing {
        route("webhook") {
            post("/github") {
                val text = call.receiveText()
                call.respondText(text)
            }
        }
    }
}

fun Application.initializeDependencies() {
    startKoin {
        modules(FrameworkModule, ApplicationModule)
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