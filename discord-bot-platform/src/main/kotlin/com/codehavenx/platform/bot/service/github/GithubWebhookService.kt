package com.codehavenx.platform.bot.service.github

import com.cramsan.framework.preferences.Preferences

class GithubWebhookService(
    private val preferences: Preferences,
) {

    fun registerWebhookEventToChannel(webhookEvent: WebhookEvent, channelId: String) {
        preferences.saveString("$CHANNEL_ID_KEY$webhookEvent", channelId)
    }

    fun getWebhookEventChannel(webhookEvent: WebhookEvent): String? {
        return preferences.loadString("$CHANNEL_ID_KEY$webhookEvent")
    }

    companion object {
        private const val TAG = ""

        private const val CHANNEL_ID_KEY = "CHANNEL_ID_KEY"
    }
}
