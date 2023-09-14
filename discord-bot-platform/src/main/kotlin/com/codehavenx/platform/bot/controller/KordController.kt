package com.codehavenx.platform.bot.controller

import com.codehavenx.platform.bot.controller.kord.InteractionModule
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class KordController(
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
                it.onGlobalChatInputRegister(),
            )
        }

        // Configure the callback for any registered commands.
        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            logI(TAG, "GuildChatInputCommandInteractionCreateEvent received")
            val command = interaction.command.rootName
            val module = moduleMap[command]
            val deferredResponse = interaction.deferPublicResponse()

            if (module == null) {
                logW(TAG, "Unrecognized command $command")
                deferredResponse.respond {
                    content = "Unrecognized command :("
                }
            } else {
                module.onGlobalChatInteraction(interaction)
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

    suspend fun sendMessage(channelId: String, builder: UserMessageCreateBuilder.() -> Unit) {
        kord.rest.channel.createMessage(Snowflake(channelId), builder)
    }

    companion object {
        private const val TAG = "KordController"
    }
}
