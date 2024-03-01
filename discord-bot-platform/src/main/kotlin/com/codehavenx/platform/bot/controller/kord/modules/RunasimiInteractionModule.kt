package com.codehavenx.platform.bot.controller.kord.modules

import com.codehavenx.platform.bot.controller.kord.InteractionModule
import com.codehavenx.platform.bot.service.runasimi.RunasimiService
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import dev.kord.common.Locale
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import io.ktor.client.request.forms.ChannelProvider
import io.ktor.utils.io.ByteReadChannel
import java.util.concurrent.CancellationException

/**
 * Module to define an interaction to interact with the Runasimi endpoint.
 */
class RunasimiInteractionModule(
    private val runasimiService: RunasimiService
) : InteractionModule {

    override val command = COMMAND

    override val description = DESCRIPTION_EN

    override val commandLocalizations = mapOf<Locale, String>()

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
            required = false
            nameLocalizations = mutableMapOf(
                Locale.SPANISH_SPAIN to ARG_2_ES,
                Locale.SPANISH_LATIN_AMERICA to ARG_2_ES,
            )
            descriptionLocalizations = mutableMapOf(
                Locale.SPANISH_SPAIN to ARG_2_DESC_ES,
                Locale.SPANISH_LATIN_AMERICA to ARG_2_DESC_ES,
            )
            SUPPORTED_VARIANTS.forEach {
                choice(it.name, it.code)
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
            val variant = command.strings.getOrDefault(ARG_2_EN, null) ?: "quy"

            if (message.length > CHAR_SIZE_LIMIT) {
                return {
                    content = when (interaction.locale) {
                        Locale.SPANISH_SPAIN, Locale.SPANISH_LATIN_AMERICA -> {
                            ERROR_MESSAGE_TOO_LONG_DESC_ES
                        }
                        else -> {
                            ERROR_MESSAGE_TOO_LONG_DESC_EN
                        }
                    }
                }
            }

            val result = runasimiService.fetchAudioFile(message, variant)

            val builder: InteractionResponseModifyBuilder.() -> Unit = {
                if (result != null) {
                    content = message

                    addFile(
                        "${sanitizeMessage(message)}.ogg",
                        ChannelProvider(
                            result.size.toLong(),
                        ) {
                            ByteReadChannel(result)
                        }
                    )
                } else {
                    content = when (interaction.locale) {
                        Locale.SPANISH_SPAIN, Locale.SPANISH_LATIN_AMERICA -> {
                            ERROR_MESSAGE_UNEXPECTED_ERROR_DESC_ES
                        }
                        else -> {
                            ERROR_MESSAGE_UNEXPECTED_ERROR_DESC_EN
                        }
                    }
                }
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

    private fun sanitizeMessage(message: String): String {
        return message.lowercase().replace(" ", "_")
    }

    companion object {
        private const val TAG = "RunasimiInteractionModule"
        private const val CHAR_SIZE_LIMIT = 50

        const val COMMAND = "rimanki"
        const val DESCRIPTION_EN = "Generate TTS audio from a text in Quechua"
        const val DESCRIPTION_ES = "Genera audio TTS a partir de un texto en Quechua"

        const val ARG_1_EN = "message"
        const val ARG_1_ES = "mensaje"
        const val ARG_1_DESC_EN = "Max $CHAR_SIZE_LIMIT characters"
        const val ARG_1_DESC_ES = "Maximo $CHAR_SIZE_LIMIT caracteres"

        const val ARG_2_EN = "variant"
        const val ARG_2_ES = "variante"
        const val ARG_2_DESC_EN = "The variant of Quechua to use"
        const val ARG_2_DESC_ES = "La variante Quechua a utilizar"

        const val ERROR_MESSAGE_TOO_LONG_DESC_EN = "Message is too long."
        const val ERROR_MESSAGE_TOO_LONG_DESC_ES = "El mensaje es demasiado largo."

        const val ERROR_MESSAGE_UNEXPECTED_ERROR_DESC_EN = "There was an unexpected error. Please try again later " +
            "with a different message or a shorter message."
        const val ERROR_MESSAGE_UNEXPECTED_ERROR_DESC_ES = "Ocucrrió un error inesperado. Por favor intenta de nuevo " +
            "más tarde con un mensaje diferente o un mensage más corto."

        private val SUPPORTED_VARIANTS = listOf(
            Variant("quy", "Ayacucho"),
            Variant("qvc", "Cajamarca"),
            Variant("quz", "Cusco"),
            Variant("qve", "Este de Apurímac"),
            Variant("qub", "Huallaga"),
            Variant("qvh", "Huamalíes-Dos de Mayo Huánuco"),
            Variant("qwh", "Huaylas Ancash"),
            Variant("qvw", "Huaylla Wanca"),
            Variant("quf", "Lambayeque"),
            Variant("qvm", "Margos-Yarowilca-Lauricocha"),
            Variant("qul", "Norte de Bolivia"),
            Variant("qvn", "Norte de Junín"),
            Variant("qxn", "Conchucos Norte, Ancash"),
            Variant("qxh", "Panao"),
            Variant("qvs", "San Martín"),
            Variant("quh", "Sur de Bolivian"),
            Variant("qxo", "Conchucos, Sur"),
        )
    }

    class Variant(val code: String, val name: String)
}
