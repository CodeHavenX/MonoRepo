package com.codehavenx.platform.bot.controller.kord

import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * This controller provides a mechanism to access Discord functionality. A [kord] is required to provide the underlying
 * API. A [globalScope] is required to handle coroutines that may happen outside of the scope of the interaction. The
 * [modules] is a list of [InteractionModule] that will be loaded.
 */
class DiscordController(
    private val kord: Kord,
    private val globalScope: CoroutineScope,
    private val modules: List<InteractionModule>,
) {

    private val moduleMap = modules.associateBy { it.command }

    suspend fun start() {
        modules.forEach {
            kord.createGlobalChatInputCommand(
                it.command,
                it.description,
            ) {
                nameLocalizations = it.commandLocalizations.toMutableMap()
                descriptionLocalizations = it.descriptionLocalizations.toMutableMap()
                it.onGlobalChatInputRegister().invoke(this)
            }
        }

        // Configure the callback for any registered commands.
        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            logI(TAG, "GuildChatInputCommandInteractionCreateEvent received")
            val command = interaction.command.rootName
            val module = moduleMap[command]

            if (module == null) {
                val deferredResponse = interaction.deferPublicResponse()
                logW(TAG, "Unrecognized command $command")
                deferredResponse.respond {
                    content = "Unrecognized command :("
                }
            } else {
                val deferredResponse = interaction.deferPublicResponse()
                val responseBuilder = module.onGlobalChatInteraction(interaction)
                deferredResponse.respond(responseBuilder)
            }
        }

        globalScope.launch {
            // Initialize the bot
            kord.login {
                // we need to specify this to receive the content of messages
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }
    }

    companion object {
        private const val TAG = "DiscordController"
    }
}
