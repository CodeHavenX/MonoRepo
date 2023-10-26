package com.cramsan.runasimi.service.service

import com.cramsan.framework.assertlib.assertFailure
import com.cramsan.framework.core.ktor.service.DiscordService

class DiscordCommunicationService(
    private val discordService: DiscordService,
    private val channelId: String,
) {

    private val isChannelIdValid by lazy { channelId.isNotBlank() }

    suspend fun sendMessage(message: String) {
        if (!isChannelIdValid) {
            assertFailure(TAG, "Invalid channel Id")
            return
        }

        discordService.sendMessage(channelId) {
            content = message
        }
    }

    companion object {
        private const val TAG = "DiscordCommunicationService"
    }
}
