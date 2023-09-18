package com.codehavenx.platform.bot.controller.kord

import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder

/**
 * Module for defining Discord interactions using Kord.
 * To learn more see [this documentation](https://github.com/kordlib/kord/wiki/Interactions#application-commands).
 */
interface InteractionModule {

    /**
     * The slash command. This string will uniquely identify this module.
     */
    val command: String

    /**
     * Description for this command.
     */
    val description: String

    /**
     * Implement this function to register your command. For more information see the [official docs](https://github.com/kordlib/kord/wiki/Interactions#registering)
     * @sample WebhookRegisterInteractionModule.onGlobalChatInputRegister
     */
    suspend fun onGlobalChatInputRegister(): GlobalChatInputCreateBuilder.() -> Unit

    /**
     * Implement this function to define how to handle an [interaction]. This function returns a builder that defines
     * how to respond the interaction.
     * @sample WebhookRegisterInteractionModule.onGlobalChatInteraction
     */
    suspend fun onGlobalChatInteraction(
        interaction: GuildChatInputCommandInteraction,
    ): InteractionResponseModifyBuilder.() -> Unit
}
