package com.cramsan.framework.core.ktor

import com.cramsan.framework.assertlib.assertFailure
import com.cramsan.framework.core.ktor.service.DiscordService
import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.Severity
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

class DiscordLogContext(
    val discordService: DiscordService,
    val channelId: String,
    val coroutineScope: CoroutineScope,
)

fun Application.initializeDiscordMonitoring(discordLogContext: DiscordLogContext, tag: String) {
    environment.monitor.subscribe(ApplicationStarting) {
        logDiscordMonitoring(discordLogContext, tag, "ApplicationStarting")
    }
    environment.monitor.subscribe(ApplicationStarted) {
        logDiscordMonitoring(discordLogContext, tag, "ApplicationStarted")
    }
    environment.monitor.subscribe(ServerReady) {
        logDiscordMonitoring(discordLogContext, tag, "ServerReady")
    }
    environment.monitor.subscribe(ApplicationStopPreparing) {
        logDiscordMonitoring(discordLogContext, tag, "ApplicationStopPreparing")
    }
    environment.monitor.subscribe(ApplicationStopping) {
        logDiscordMonitoring(discordLogContext, tag, "ApplicationStopping")
    }
    environment.monitor.subscribe(ApplicationStopped) {
        logDiscordMonitoring(discordLogContext, tag, "ApplicationStopped")
    }
}

private fun logDiscordMonitoring(
    discordLogContext: DiscordLogContext,
    tag: String,
    message: String,
) {
    val discordService = discordLogContext.discordService
    val channelId = discordLogContext.channelId
    val coroutineScope = discordLogContext.coroutineScope

    coroutineScope.launch {
        discordService.sendMessage(channelId) { content = "[$tag]$message" }
    }
}