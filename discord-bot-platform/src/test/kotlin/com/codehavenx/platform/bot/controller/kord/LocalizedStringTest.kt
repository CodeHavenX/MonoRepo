package com.codehavenx.platform.bot.controller.kord

import dev.kord.common.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizedStringTest {

    @Test
    fun `toLanguage returns default when language is null`() {
        val localizedString = LocalizedString(
            default = "default",
            english = "english",
            spanish = "spanish",
        )

        val result = localizedString.toLanguage(null)

        assertEquals("default", result)
    }

    @Test
    fun `toLanguage returns english when language is ENGLISH_GREAT_BRITAIN`() {
        val localizedString = LocalizedString(
            default = "default",
            english = "english",
            spanish = "spanish",
        )

        val result = localizedString.toLanguage(Locale.ENGLISH_GREAT_BRITAIN)

        assertEquals("english", result)
    }

    @Test
    fun `toLanguage returns english when language is ENGLISH_UNITED_STATES`() {
        val localizedString = LocalizedString(
            default = "default",
            english = "english",
            spanish = "spanish",
        )

        val result = localizedString.toLanguage(Locale.ENGLISH_UNITED_STATES)

        assertEquals("english", result)
    }

    @Test
    fun `toLanguage returns spanish when language is SPANISH_LATIN_AMERICA`() {
        val localizedString = LocalizedString(
            default = "default",
            english = "english",
            spanish = "spanish",
        )

        val result = localizedString.toLanguage(Locale.SPANISH_LATIN_AMERICA)

        assertEquals("spanish", result)
    }

    @Test
    fun `toLanguage returns spanish when language is SPANISH_SPAIN`() {
        val localizedString = LocalizedString(
            default = "default",
            english = "english",
            spanish = "spanish",
        )

        val result = localizedString.toLanguage(Locale.SPANISH_SPAIN)

        assertEquals("spanish", result)
    }

    @Test
    fun `map is initialized correctly`() {
        val localizedString = LocalizedString(
            default = "default",
            english = "english",
            spanish = "spanish",
        )

        assertEquals("english", localizedString.map[Locale.ENGLISH_GREAT_BRITAIN])
        assertEquals("english", localizedString.map[Locale.ENGLISH_UNITED_STATES])
        assertEquals("spanish", localizedString.map[Locale.SPANISH_SPAIN])
        assertEquals("spanish", localizedString.map[Locale.SPANISH_LATIN_AMERICA])
    }
}
