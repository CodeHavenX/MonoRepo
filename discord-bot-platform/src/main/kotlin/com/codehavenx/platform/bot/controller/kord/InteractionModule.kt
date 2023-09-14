package com.codehavenx.platform.bot.controller.kord

import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder

interface InteractionModule {

    val command: String

    val description: String

    suspend fun onGlobalChatInputRegister(): GlobalChatInputCreateBuilder.() -> Unit

    suspend fun onGlobalChatInteraction(interaction: GuildChatInputCommandInteraction)
}
