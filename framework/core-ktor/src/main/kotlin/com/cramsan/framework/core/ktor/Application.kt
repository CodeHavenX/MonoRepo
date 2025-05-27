package com.cramsan.framework.core.ktor

import com.cramsan.framework.logging.EventLoggerInterface
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.ServerReady
import io.ktor.server.response.respond
import io.ktor.server.routing.head
import io.ktor.server.routing.routing

/**
 * Initialize monitoring
 */
fun Application.initializeMonitoring(
    tag: String,
    logger: EventLoggerInterface,
) {
    monitor.subscribe(ApplicationStarting) {
        logger.i(tag, "ApplicationStarting")
    }
    monitor.subscribe(ApplicationStarted) {
        logger.i(tag, "ApplicationStarted")
    }
    monitor.subscribe(ServerReady) {
        logger.i(tag, "ServerReady")
    }
    monitor.subscribe(ApplicationStopPreparing) {
        logger.i(tag, "ApplicationStopPreparing")
    }
    monitor.subscribe(ApplicationStopping) {
        logger.i(tag, "ApplicationStopping")
    }
    monitor.subscribe(ApplicationStopped) {
        logger.i(tag, "ApplicationStopped")
    }
}

/**
 * Configure an endpoint to check for the application health.
 */
fun Application.configureHealthEndpoint() {
    routing {
        head("/health") {
            call.respond(HttpStatusCode.OK, "OK")
        }
    }
}
