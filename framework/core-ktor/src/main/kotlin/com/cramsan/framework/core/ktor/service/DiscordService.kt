package com.cramsan.framework.core.ktor.service

import com.cramsan.framework.logging.logD
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder

class DiscordService(
    private val kord: Kord,
) {
    suspend fun sendMessage(channelId: String, builder: UserMessageCreateBuilder.() -> Unit) {
        logD(TAG, "Sending message to channelId")
        kord.rest.channel.createMessage(Snowflake(channelId), builder)
    }

    companion object {
        private const val TAG = "DiscordService"
    }
}
