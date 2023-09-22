package com.codehavenx.platform.bot.service

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder

class DiscordService(
    private val kord: Kord,
) {
    suspend fun sendMessage(channelId: String, builder: UserMessageCreateBuilder.() -> Unit) {
        kord.rest.channel.createMessage(Snowflake(channelId), builder)
    }

    companion object {
        private const val TAG = "DiscordService"
    }
}
