package com.codehavenx.platform.bot.controller.kord

import dev.kord.common.Locale
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder

/**
 * A localized argument for a command defined in a [GlobalChatInputCreateBuilder].
 */
class LocalizedArgument(
    localizedName: LocalizedString,
    localizedDescription: LocalizedString,
) {
    /**
     * Default name for the argument
     */
    val name: String = localizedName.default

    /**
     * Default description for the argument
     */
    val description: String = localizedDescription.default

    private val _localizedName: MutableMap<Locale, String> = mutableMapOf()

    /**
     * Localized names for the argument.
     */
    val localizedName: MutableMap<Locale, String>
        get() = _localizedName.toMutableMap()

    private val _localizedDescription: MutableMap<Locale, String> = mutableMapOf()

    /**
     * Localized descriptions for the argument.
     */
    val localizedDescription: MutableMap<Locale, String>
        get() = _localizedDescription.toMutableMap()

    init {
        _localizedName.putAll(localizedName.map)
        _localizedDescription.putAll(localizedDescription.map)
    }
}
