package com.cramsan.framework.core.ktor

import com.cramsan.framework.logging.logI
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
    monitor.subscribe(ApplicationStarting) {
        logI(tag, "ApplicationStarting")
    }
    monitor.subscribe(ApplicationStarted) {
        logI(tag, "ApplicationStarted")
    }
    monitor.subscribe(ServerReady) {
        logI(tag, "ServerReady")
    }
    monitor.subscribe(ApplicationStopPreparing) {
        logI(tag, "ApplicationStopPreparing")
    }
    monitor.subscribe(ApplicationStopping) {
        logI(tag, "ApplicationStopping")
    }
    monitor.subscribe(ApplicationStopped) {
        logI(tag, "ApplicationStopped")
    }
}
