package com.codehavenx.platform.bot.controller.kord.modules

import com.codehavenx.platform.bot.controller.kord.InteractionModule
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

    override val command = COMMAND_EN

    override val description = DESCRIPTION_EN

    override val commandLocalizations = mapOf(
        Locale.SPANISH_SPAIN to COMMAND_ES,
        Locale.SPANISH_LATIN_AMERICA to COMMAND_ES,
    )

    override val descriptionLocalizations = mapOf(
        Locale.SPANISH_SPAIN to DESCRIPTION_ES,
        Locale.SPANISH_LATIN_AMERICA to DESCRIPTION_ES,
    )

    override suspend fun onGlobalChatInputRegister(): GlobalChatInputCreateBuilder.() -> Unit = {
        string(ARG_1_EN, ARG_1_DESC_EN) {
            required = true
            nameLocalizations = mutableMapOf(
                Locale.SPANISH_SPAIN to ARG_1_ES,
                Locale.SPANISH_LATIN_AMERICA to ARG_1_ES,
            )
            descriptionLocalizations = mutableMapOf(
                Locale.SPANISH_SPAIN to ARG_1_DESC_ES,
                Locale.SPANISH_LATIN_AMERICA to ARG_1_DESC_ES,
            )
        }

        string(ARG_2_EN, ARG_2_DESC_EN) {
            required = true
            nameLocalizations = mutableMapOf(
                Locale.SPANISH_SPAIN to ARG_2_ES,
                Locale.SPANISH_LATIN_AMERICA to ARG_2_ES,
            )
            descriptionLocalizations = mutableMapOf(
                Locale.SPANISH_SPAIN to ARG_2_DESC_ES,
                Locale.SPANISH_LATIN_AMERICA to ARG_2_DESC_ES,
            )
            supportedLanguages.forEach {
                val language = it.key
                val name = it.value[Locale.ENGLISH_GREAT_BRITAIN]
                    ?: it.value[Locale.ENGLISH_UNITED_STATES]
                    ?: language.code
                choice(name, language.code, Optional.invoke(it.value))
            }
        }

        string(ARG_3_EN, ARG_3_DESC_EN) {
            required = true
            nameLocalizations = mutableMapOf(
                Locale.SPANISH_SPAIN to ARG_3_ES,
                Locale.SPANISH_LATIN_AMERICA to ARG_3_ES,
            )
            descriptionLocalizations = mutableMapOf(
                Locale.SPANISH_SPAIN to ARG_3_DESC_ES,
                Locale.SPANISH_LATIN_AMERICA to ARG_3_DESC_ES,
            )
            supportedLanguages.forEach {
                val language = it.key
                val name = it.value[Locale.ENGLISH_GREAT_BRITAIN]
                    ?: it.value[Locale.ENGLISH_UNITED_STATES]
                    ?: language.code
                choice(name, language.code, Optional.invoke(it.value))
            }
        }
    }

    @Suppress("SwallowedException")
    override suspend fun onGlobalChatInteraction(
        interaction: GuildChatInputCommandInteraction,
    ): InteractionResponseModifyBuilder.() -> Unit {
        logD(TAG, "Received event data: %S", interaction)

        val command = interaction.command
        val message = command.strings.getValue(ARG_1_EN)

        return try {
            val originLang = Language.fromCode(
                command.strings.getValue(ARG_2_EN)
            ) ?: throw IllegalArgumentException("Invalid origin language")
            val resultLang = Language.fromCode(
                command.strings.getValue(ARG_3_EN)
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

        const val COMMAND_EN = "translate"
        const val COMMAND_ES = "traducir"
        const val DESCRIPTION_EN = "Translate a message from one language to another"
        const val DESCRIPTION_ES = "Traduce un mensaje de un idioma a otro"

        const val ARG_1_EN = "message"
        const val ARG_1_ES = "mensaje"
        const val ARG_1_DESC_EN = "Max 120 characters"
        const val ARG_1_DESC_ES = "Maximo 120 caracteres"

        const val ARG_2_EN = "from"
        const val ARG_2_ES = "del"
        const val ARG_2_DESC_EN = "Message language"
        const val ARG_2_DESC_ES = "Lenguaje del mensaje"

        const val ARG_3_EN = "to"
        const val ARG_3_ES = "al"
        const val ARG_3_DESC_EN = "Output language"
        const val ARG_3_DESC_ES = "Lenguaje del cual traducir"

        const val LANG_ES_ES = "Español"
        const val LANG_ES_EN = "Spanish"

        const val LANG_EN_ES = "Ingles"
        const val LANG_EN_EN = "English"

        const val LANG_QU = "Quechua"

        private val supportedLanguages = mapOf(
            Language.ENGLISH to mapOf(
                Locale.ENGLISH_GREAT_BRITAIN to LANG_EN_EN,
                Locale.ENGLISH_UNITED_STATES to LANG_EN_EN,
                Locale.SPANISH_SPAIN to LANG_EN_ES,
                Locale.SPANISH_LATIN_AMERICA to LANG_EN_ES,
            ),
            Language.SPANISH to mapOf(
                Locale.ENGLISH_GREAT_BRITAIN to LANG_ES_EN,
                Locale.ENGLISH_UNITED_STATES to LANG_ES_EN,
                Locale.SPANISH_SPAIN to LANG_ES_ES,
                Locale.SPANISH_LATIN_AMERICA to LANG_ES_ES,
            ),
            Language.QUECHUA to mapOf(
                Locale.ENGLISH_GREAT_BRITAIN to LANG_QU,
                Locale.ENGLISH_UNITED_STATES to LANG_QU,
                Locale.SPANISH_SPAIN to LANG_QU,
                Locale.SPANISH_LATIN_AMERICA to LANG_QU,

            ),
        )
    }
}
