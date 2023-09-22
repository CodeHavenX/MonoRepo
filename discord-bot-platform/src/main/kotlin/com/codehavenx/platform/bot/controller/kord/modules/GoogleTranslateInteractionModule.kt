package com.codehavenx.platform.bot.controller.kord.modules

import com.codehavenx.platform.bot.controller.kord.InteractionModule
import com.codehavenx.platform.bot.service.google.GoogleTranslateService
import com.codehavenx.platform.bot.service.google.Language
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import java.util.concurrent.CancellationException

/**
 * Module to define an interaction to translate a message using Google Translate API.
 */
class GoogleTranslateInteractionModule(
    private val googleTranslateService: GoogleTranslateService,
) : InteractionModule {

    override val command = "runasimi"

    override val description = "Translate a message from spanish to quechua"
    override suspend fun onGlobalChatInputRegister(): GlobalChatInputCreateBuilder.() -> Unit = {
        string("message", "Max 120 characters") {
            required = true
        }
    }

    @Suppress("SwallowedException")
    override suspend fun onGlobalChatInteraction(
        interaction: GuildChatInputCommandInteraction,
    ): InteractionResponseModifyBuilder.() -> Unit {
        logD(TAG, "Received event data: $interaction",)

        val command = interaction.command
        val message = command.strings.getValue("message")

        return try {
            val result = googleTranslateService.translate(message, Language.QUECHUA, Language.SPANISH)

            val builder: InteractionResponseModifyBuilder.() -> Unit = {
                content = "En español: $message\nChaynatam ninku: $result"
            }
            builder
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            logE(TAG, "Exception while translating  data: $message", throwable)
            val builder: InteractionResponseModifyBuilder.() -> Unit = {
                content = "There was an exception \uD83D\uDE31"
            }
            builder
        }
    }

    companion object {
        private const val TAG = "GoogleTranslateInteractionModule"
    }
}
