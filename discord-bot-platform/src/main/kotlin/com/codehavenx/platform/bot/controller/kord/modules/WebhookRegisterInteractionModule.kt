package com.codehavenx.platform.bot.controller.kord.modules

import com.codehavenx.platform.bot.controller.kord.InteractionModule
import com.codehavenx.platform.bot.service.github.GithubWebhookService
import com.codehavenx.platform.bot.service.github.WebhookEvent
import com.cramsan.framework.logging.logD
import dev.kord.common.Locale
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import java.util.concurrent.CancellationException

/**
 * Module to define an interaction to register a webhook type [WebhookEvent] with a Discord channel.
 */
class WebhookRegisterInteractionModule(
    private val githubWebhookService: GithubWebhookService,
) : InteractionModule {

    override val command = "wh_register"

    override val description = "Register a webhook to the current channel"

    override val commandLocalizations = emptyMap<Locale, String>()

    override val descriptionLocalizations = emptyMap<Locale, String>()

    override suspend fun onGlobalChatInputRegister(): GlobalChatInputCreateBuilder.() -> Unit = {
        string("event", "Event to register in this channel") {
            required = true
            WebhookEvent.values().forEach {
                choice(it.name, it.name)
            }
        }
    }

    @Suppress("SwallowedException")
    override suspend fun onGlobalChatInteraction(
        interaction: GuildChatInputCommandInteraction,
    ): InteractionResponseModifyBuilder.() -> Unit {
        logD(TAG, "Received event data: %S", interaction)

        val command = interaction.command
        val webhookEventParam = command.strings.getValue("event")
        val webhookEvent = WebhookEvent.valueOf(webhookEventParam)

        return try {
            githubWebhookService.registerWebhookEventToChannel(
                webhookEvent,
                interaction.channelId.toString(),
            )

            val builder: InteractionResponseModifyBuilder.() -> Unit = {
                content = "This channel will now receive events of type $webhookEvent"
            }
            builder
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            {
                content = "There was an exception \uD83D\uDE31"
            }
        }
    }

    companion object {
        private const val TAG = "WebHookRegisterInteractionModule"
    }
}
