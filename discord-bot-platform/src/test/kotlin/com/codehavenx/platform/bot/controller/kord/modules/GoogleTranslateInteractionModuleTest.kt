package com.codehavenx.platform.bot.controller.kord.modules

import com.codehavenx.platform.bot.service.google.GoogleTranslateService
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.test.TestBase
import dev.kord.common.Locale
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class GoogleTranslateInteractionModuleTest : TestBase() {

    @MockK
    lateinit var googleTranslateService: GoogleTranslateService

    private lateinit var module: GoogleTranslateInteractionModule

    override fun setupTest() {
        MockKAnnotations.init(this) // turn relaxUnitFun on for all mocks
        EventLogger.setInstance(mockk(relaxed = true))
        module = GoogleTranslateInteractionModule(
            googleTranslateService,
        )
    }

    @Test
    fun `verify localizations`() {
        assertEquals("translate", module.command)
        assertEquals(
            mutableMapOf(
                Locale.SPANISH_SPAIN to "traducir",
                Locale.SPANISH_LATIN_AMERICA to "traducir",
            ),
            module.commandLocalizations
        )
        assertEquals("Translate a message from one language to another", module.description)
        assertEquals(
            mutableMapOf(
                Locale.SPANISH_SPAIN to "Traduce un mensaje de un idioma a otro",
                Locale.SPANISH_LATIN_AMERICA to "Traduce un mensaje de un idioma a otro",
            ),
            module.descriptionLocalizations
        )
    }
}
