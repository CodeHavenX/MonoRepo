package com.codehavenx.platform.bot.controller.kord.modules

import com.codehavenx.platform.bot.controller.kord.InteractionModule
import com.codehavenx.platform.bot.controller.kord.LocalizedArgument
import com.codehavenx.platform.bot.controller.kord.LocalizedString
import com.codehavenx.platform.bot.service.google.GoogleTranslateService
import com.codehavenx.platform.bot.service.google.Language
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import dev.kord.common.Locale
import dev.kord.common.entity.optional.Optional
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

    override val command = localizedCommand.default

    override val description = localizedDescription.default

    override val commandLocalizations = localizedCommand.map

    override val descriptionLocalizations = localizedDescription.map

    override suspend fun onGlobalChatInputRegister(): GlobalChatInputCreateBuilder.() -> Unit = {
        string(argumentMessage.name, argumentMessage.description) {
            required = true
            nameLocalizations = argumentMessage.localizedName
            descriptionLocalizations = argumentMessage.localizedDescription
        }

        string(argumentFrom.name, argumentFrom.description) {
            required = true
            nameLocalizations = argumentFrom.localizedName
            descriptionLocalizations = argumentFrom.localizedDescription
            supportedLanguages.forEach {
                val language = it.key
                val localizedString = it.value
                val name = localizedString.map[Locale.ENGLISH_GREAT_BRITAIN]
                    ?: localizedString.map[Locale.ENGLISH_UNITED_STATES]
                    ?: language.code
                choice(name, language.code, Optional.invoke(localizedString.map))
            }
        }

        string(argumentTo.name, argumentTo.description) {
            required = true
            nameLocalizations = argumentTo.localizedName
            descriptionLocalizations = argumentTo.localizedDescription
            supportedLanguages.forEach {
                val language = it.key
                val localizedString = it.value
                val name = localizedString.map[Locale.ENGLISH_GREAT_BRITAIN]
                    ?: localizedString.map[Locale.ENGLISH_UNITED_STATES]
                    ?: language.code
                choice(name, language.code, Optional.invoke(localizedString.map))
            }
        }
    }

    @Suppress("SwallowedException")
    override suspend fun onGlobalChatInteraction(
        interaction: GuildChatInputCommandInteraction,
    ): InteractionResponseModifyBuilder.() -> Unit {
        logD(TAG, "Received event data: %S", interaction)

        val command = interaction.command
        val message = command.strings.getValue(argumentMessage.name)

        return try {
            val originLang = Language.fromCode(
                command.strings.getValue(argumentFrom.name)
            ) ?: throw IllegalArgumentException("Invalid origin language")
            val resultLang = Language.fromCode(
                command.strings.getValue(argumentTo.name)
            ) ?: throw IllegalArgumentException("Invalid result language")

            val result = googleTranslateService.translate(message, originLang, resultLang)

            val builder: InteractionResponseModifyBuilder.() -> Unit = {
                content = "${originLang.code}: $message\n${resultLang.code}: $result"
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

        private val localizedCommand = LocalizedString(
            default = "translate",
            spanish = "traducir",
        )

        private val localizedDescription = LocalizedString(
            default = "Translate a message from one language to another",
            spanish = "Traduce un mensaje de un idioma a otro",
        )

        private val argumentMessage = LocalizedArgument(
            localizedName = LocalizedString(
                default = "message",
                spanish = "mensaje",
            ),
            localizedDescription = LocalizedString(
                default = "Max 120 characters",
                spanish = "Maximo 120 caracteres",
            ),
        )

        private val argumentFrom = LocalizedArgument(
            localizedName = LocalizedString(
                default = "from",
                spanish = "del",
            ),
            localizedDescription = LocalizedString(
                default = "Message language",
                spanish = "Lenguaje del mensaje",
            ),
        )

        private val argumentTo = LocalizedArgument(
            localizedName = LocalizedString(
                default = "to",
                spanish = "al",
            ),
            localizedDescription = LocalizedString(
                default = "Output language",
                spanish = "Lenguaje al cual traducir",
            ),
        )

        private val supportedLanguages = mapOf(
            Language.ENGLISH to LocalizedString(
                "English",
                "Ingles",
            ),
            Language.SPANISH to LocalizedString(
                "Spanish",
                "Español",
            ),
            Language.QUECHUA to LocalizedString(
                "Quechua",
                "Quechua",
            ),
        )
    }
}
