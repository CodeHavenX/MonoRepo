package com.codehavenx.platform.bot.controller.kord.modules

import com.codehavenx.platform.bot.controller.kord.InteractionModule
import com.codehavenx.platform.bot.controller.kord.LocalizedArgument
import com.codehavenx.platform.bot.controller.kord.LocalizedString
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

    override val command = "rimanki"

    override val description = localizedDescription.default

    override val commandLocalizations = mapOf<Locale, String>()

    override val descriptionLocalizations = localizedDescription.map

    override suspend fun onGlobalChatInputRegister(): GlobalChatInputCreateBuilder.() -> Unit = {
        string(argumentMessage.name, argumentMessage.description) {
            required = true
            nameLocalizations = argumentMessage.localizedName
            descriptionLocalizations = argumentMessage.localizedDescription
        }

        string(argumentVariant.name, argumentVariant.description) {
            required = false
            nameLocalizations = argumentVariant.localizedName
            descriptionLocalizations = argumentVariant.localizedDescription
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
        val message = command.strings.getValue(argumentMessage.name)

        return try {
            val variant = command.strings.getOrDefault(argumentVariant.name, null) ?: "quy"

            if (message.length > CHAR_SIZE_LIMIT) {
                return {
                    content = ERROR_MESSAGE_TOO_LONG_DESC.toLanguage(interaction.locale)
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
                    content = ERROR_MESSAGE_UNEXPECTED_ERROR_DESC.toLanguage(interaction.locale)
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

        private val localizedDescription = LocalizedString(
            default = "Generate TTS audio from a text in Quechua",
            spanish = "Genera audio TTS a partir de un texto en Quechua",
        )

        private val argumentMessage = LocalizedArgument(
            localizedName = LocalizedString(
                default = "message",
                spanish = "mensaje",
            ),
            localizedDescription = LocalizedString(
                default = "Max $CHAR_SIZE_LIMIT characters",
                spanish = "Maximo $CHAR_SIZE_LIMIT caracteres",
            ),
        )

        private val argumentVariant = LocalizedArgument(
            localizedName = LocalizedString(
                default = "variant",
                spanish = "variante",
            ),
            localizedDescription = LocalizedString(
                default = "The variant of Quechua to use",
                spanish = "La variante Quechua a utilizar",
            ),
        )

        private val ERROR_MESSAGE_TOO_LONG_DESC = LocalizedString(
            default = "Message is too long.",
            spanish = "El mensaje es demasiado largo.",
        )

        private val ERROR_MESSAGE_UNEXPECTED_ERROR_DESC = LocalizedString(
            default = "There was an unexpected error. Please try again later " +
                "with a different message or a shorter message.",
            spanish = "Ocucrrió un error inesperado. Por favor intenta de nuevo " +
                "más tarde con un mensaje diferente o un mensage más corto."
        )

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

        private class Variant(val code: String, val name: String)
    }
}
