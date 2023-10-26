package com.cramsan.framework.core.ktor

import com.cramsan.framework.core.ktor.service.DiscordService
import com.cramsan.framework.logging.logI
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.ServerReady
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

fun Application.initializeDiscordMonitoring(
    discordService: DiscordService,
    channelId: String,
    coroutineScope: CoroutineScope,
    tag: String,
) {
    environment.monitor.subscribe(ApplicationStarting) {
        logDiscordMonitoring(discordService, channelId, coroutineScope, tag, "ApplicationStarting")
    }
    environment.monitor.subscribe(ApplicationStarted) {
        logDiscordMonitoring(discordService, channelId, coroutineScope, tag, "ApplicationStarted")
    }
    environment.monitor.subscribe(ServerReady) {
        logDiscordMonitoring(discordService, channelId, coroutineScope, tag, "ServerReady")
    }
    environment.monitor.subscribe(ApplicationStopPreparing) {
        logDiscordMonitoring(discordService, channelId, coroutineScope, tag, "ApplicationStopPreparing")
    }
    environment.monitor.subscribe(ApplicationStopping) {
        logDiscordMonitoring(discordService, channelId, coroutineScope, tag, "ApplicationStopping")
    }
    environment.monitor.subscribe(ApplicationStopped) {
        logDiscordMonitoring(discordService, channelId, coroutineScope, tag, "ApplicationStopped")
    }
}

private fun logDiscordMonitoring(
    discordService: DiscordService,
    channelId: String,
    coroutineScope: CoroutineScope,
    tag: String,
    message: String,
) {
    coroutineScope.launch {
        discordService.sendMessage(channelId) { content = "[$tag]$message" }
    }
}
