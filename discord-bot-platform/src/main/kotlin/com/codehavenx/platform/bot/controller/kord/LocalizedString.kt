package com.codehavenx.platform.bot.controller.kord

import dev.kord.common.Locale

/**
 * A representation of a localized string. The [default] value is always required but other languages optional.
 * For any language that the value is not specified, the [default] value will be used.
 */
class LocalizedString(
    val default: String,
    english: String? = null,
    spanish: String? = null,
) {

    /**
     * Map of localized strings for different languages. The key is the language and the value is the string.
     * The [default] value is not included in this map.
     */
    private val _map = mutableMapOf<Locale, String>()
    val map: MutableMap<Locale, String>
        get() = _map.toMutableMap()

    init {
        english?.let {
            _map[Locale.ENGLISH_GREAT_BRITAIN] = it
            _map[Locale.ENGLISH_UNITED_STATES] = it
        }
        spanish?.let {
            _map[Locale.SPANISH_SPAIN] = it
            _map[Locale.SPANISH_LATIN_AMERICA] = it
        }
    }

    /**
     * Retrieves the localized string in the provided [language]. If the language is not specified or there is not a
     * localized value, the [default] value will be used.
     */
    fun toLanguage(language: Locale?): String = language?.let { map[language] } ?: default
}
