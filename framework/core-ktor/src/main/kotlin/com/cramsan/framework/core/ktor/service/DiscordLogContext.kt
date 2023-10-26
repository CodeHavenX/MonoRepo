package com.cramsan.framework.core.ktor.service

import kotlinx.coroutines.CoroutineScope

class DiscordLogContext(
    val discordService: DiscordService,
    val channelId: String,
    val coroutineScope: CoroutineScope,
)
