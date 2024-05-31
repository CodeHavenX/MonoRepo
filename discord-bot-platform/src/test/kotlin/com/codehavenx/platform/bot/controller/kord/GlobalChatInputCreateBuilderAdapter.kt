package com.codehavenx.platform.bot.controller.kord

import dev.kord.common.Locale
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.json.request.ApplicationCommandCreateRequest

class GlobalChatInputCreateBuilderAdapter(
    override var defaultMemberPermissions: Permissions? = null,
    override var defaultPermission: Boolean? = null,
    override var nsfw: Boolean? = null,
    override val type: ApplicationCommandType = ApplicationCommandType.ChatInput,
    override var options: MutableList<OptionsBuilder>? = null,
    override var dmPermission: Boolean? = null,
    override var descriptionLocalizations: MutableMap<Locale, String>? = null,
    override var description: String = "",
    override var nameLocalizations: MutableMap<Locale, String>? = null,
    override var name: String = ""
) : GlobalChatInputCreateBuilder {
    override fun toRequest(): ApplicationCommandCreateRequest {
        return ApplicationCommandCreateRequest(
            name,
            Optional(),
            type,
            Optional.Value(description),
            Optional(),
            Optional(),
            Optional(),
            OptionalBoolean.Missing,
            OptionalBoolean.Missing,
            OptionalBoolean.Missing,
        )
    }
}
