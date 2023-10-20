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
    environment.monitor.subscribe(ApplicationStarting) {
        logI(tag, "ApplicationStarting")
    }
    environment.monitor.subscribe(ApplicationStarted) {
        logI(tag, "ApplicationStarted")
    }
    environment.monitor.subscribe(ServerReady) {
        logI(tag, "ServerReady")
    }
    environment.monitor.subscribe(ApplicationStopPreparing) {
        logI(tag, "ApplicationStopPreparing")
    }
    environment.monitor.subscribe(ApplicationStopping) {
        logI(tag, "ApplicationStopping")
    }
    environment.monitor.subscribe(ApplicationStopped) {
        logI(tag, "ApplicationStopped")
    }
}
