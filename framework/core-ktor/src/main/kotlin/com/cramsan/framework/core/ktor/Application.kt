package com.cramsan.framework.core.ktor

import com.cramsan.framework.logging.logW
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.ServerReady

/**
 * Initialize monitoring
 */
fun Application.initializeMonitoring(tag: String) {
    environment.monitor.subscribe(ApplicationStarting) {
        logW(tag, "ApplicationStarting")
    }
    environment.monitor.subscribe(ApplicationStarted) {
        logW(tag, "ApplicationStarted")
    }
    environment.monitor.subscribe(ServerReady) {
        logW(tag, "ServerReady")
    }
    environment.monitor.subscribe(ApplicationStopPreparing) {
        logW(tag, "ApplicationStopPreparing")
    }
    environment.monitor.subscribe(ApplicationStopping) {
        logW(tag, "ApplicationStopping")
    }
    environment.monitor.subscribe(ApplicationStopped) {
        logW(tag, "ApplicationStopped")
    }
}
