package com.codehavenx.platform.bot.controller.kord.modules

import com.codehavenx.platform.bot.service.runasimi.RunasimiService
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.test.TestBase
import dev.kord.common.Locale
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class RunasimiInteractionModuleTest : TestBase() {

    @MockK
    lateinit var runasimiService: RunasimiService

    private lateinit var module: RunasimiInteractionModule

    override fun setupTest() {
        MockKAnnotations.init(this) // turn relaxUnitFun on for all mocks
        EventLogger.setInstance(mockk(relaxed = true))
        module = RunasimiInteractionModule(
            runasimiService,
        )
    }

    @Test
    fun `verify localizations`() {
        assertEquals("rimanki", module.command)
        assertEquals(emptyMap(), module.commandLocalizations)
        assertEquals("Generate TTS audio from a text in Quechua", module.description)
        assertEquals(
            mutableMapOf(
                Locale.SPANISH_SPAIN to "Genera audio TTS a partir de un texto en Quechua",
                Locale.SPANISH_LATIN_AMERICA to "Genera audio TTS a partir de un texto en Quechua",
            ),
            module.descriptionLocalizations
        )
    }
}
