package com.codehavenx.platform.bot.controller.kord

import dev.kord.common.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizedArgumentTest {

    @Test
    fun `name returns default values`() {
        val localizedName = LocalizedString(
            default = "defaultName",
            english = "englishName",
            spanish = "spanishName",
        )
        val localizedDescription = LocalizedString(
            default = "defaultDescription",
            english = "englishDescription",
            spanish = "spanishDescription",
        )
        val localizedArgument = LocalizedArgument(localizedName, localizedDescription)

        assertEquals("defaultName", localizedArgument.name)
        assertEquals("defaultDescription", localizedArgument.description)
    }

    @Test
    fun `localizedName returns correct localized name`() {
        val localizedName = LocalizedString(
            default = "defaultName",
            english = "englishName",
            spanish = "spanishName",
        )
        val localizedDescription = LocalizedString(
            default = "defaultDescription",
            english = "englishDescription",
            spanish = "spanishDescription",
        )
        val localizedArgument = LocalizedArgument(localizedName, localizedDescription)

        assertEquals("englishName", localizedArgument.localizedName[Locale.ENGLISH_GREAT_BRITAIN])
        assertEquals("englishName", localizedArgument.localizedName[Locale.ENGLISH_UNITED_STATES])
        assertEquals("spanishName", localizedArgument.localizedName[Locale.SPANISH_SPAIN])
        assertEquals("spanishName", localizedArgument.localizedName[Locale.SPANISH_LATIN_AMERICA])
    }

    @Test
    fun `localizedDescription returns correct localized description`() {
        val localizedName = LocalizedString(
            default = "defaultName",
            english = "englishName",
            spanish = "spanishName",
        )
        val localizedDescription = LocalizedString(
            default = "defaultDescription",
            english = "englishDescription",
            spanish = "spanishDescription",
        )
        val localizedArgument = LocalizedArgument(localizedName, localizedDescription)

        assertEquals("englishDescription", localizedArgument.localizedDescription[Locale.ENGLISH_GREAT_BRITAIN])
        assertEquals("englishDescription", localizedArgument.localizedDescription[Locale.ENGLISH_UNITED_STATES])
        assertEquals("spanishDescription", localizedArgument.localizedDescription[Locale.SPANISH_SPAIN])
        assertEquals("spanishDescription", localizedArgument.localizedDescription[Locale.SPANISH_LATIN_AMERICA])
    }
}
