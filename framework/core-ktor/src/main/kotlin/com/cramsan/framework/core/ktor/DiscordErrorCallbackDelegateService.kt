package com.cramsan.framework.core.ktor

import com.cramsan.framework.assertlib.assertFailure
import com.cramsan.framework.core.ktor.service.DiscordService
import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.Severity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DiscordErrorCallbackDelegateService(
    private val discordService: DiscordService,
    private val channelId: String,
    private val coroutineScope: CoroutineScope,
) : EventLoggerErrorCallbackDelegate {

    private val isChannelIdValid by lazy { channelId.isNotBlank() }
    override fun handleErrorEvent(tag: String, message: String, throwable: Throwable, severity: Severity) {
        if (!isChannelIdValid) {
            assertFailure(TAG, "Invalid channel Id")
            return
        }

        coroutineScope.launch {
            discordService.sendMessage(channelId) {
                content = "[$tag] [$severity] - $message\n${throwable.localizedMessage}"
            }
        }
    }

    companion object {
        private const val TAG = "DiscordErrorCallbackDelegate"
    }
}
