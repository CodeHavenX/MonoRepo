package com.codehavenx.platform.bot.controller.kord

import com.codehavenx.platform.bot.service.github.GithubWebhookService
import com.codehavenx.platform.bot.service.github.WebhookEvent
import com.cramsan.framework.logging.logD
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import java.util.concurrent.CancellationException

class WebHookRegisterInteractionModule(
    private val githubWebhookService: GithubWebhookService,
) : InteractionModule {

    override val command = "wh_register"

    override val description = "Register a webhook to the current channel"
    override suspend fun onGlobalChatInputRegister(): GlobalChatInputCreateBuilder.() -> Unit = {
        string("event", "Event to register in this channel") {
            required = true
            WebhookEvent.values().forEach {
                choice(it.name, it.name)
            }
        }
    }

    @Suppress("SwallowedException")
    override suspend fun onGlobalChatInteraction(interaction: GuildChatInputCommandInteraction) {
        logD(TAG, "Received event data: $interaction",)

        val command = interaction.command
        val webhookEventParam = command.strings.getValue("event")
        val webhookEvent = WebhookEvent.valueOf(webhookEventParam)

        val deferredResponse = interaction.deferPublicResponse()
        val responseBuilder = try {
            githubWebhookService.registerWebhookEventToChannel(
                webhookEvent,
                interaction.channelId.toString()
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
        deferredResponse.respond(responseBuilder)
    }

    companion object {
        private const val TAG = "WebHookRegisterInteractionModule"
    }
}
