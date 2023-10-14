package com.cramsan.minesweepers.common

import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest

class StatementManagerTest : BaseStatementManagerTest() {


    @BeforeTest
    fun setup() {
        verbProvider = mockk()

        statementManager = StatementManager(
            verbProvider,
        )

        every { verbProvider.loadResources() } returns listOf(
            Verb(
                listOf("chirichi"),
                listOf("enfriar"),
            ),
            Verb(
                listOf("taka"),
                listOf("golpear"),
            ),
            Verb(
                listOf("rupachi"),
                listOf("encender"),
            ),
            Verb(
                listOf("tulluya"),
                listOf("enflaquecer"),
            ),
        )
    }

    override fun preGenerateStatementTest() {}

    override fun preGenerateTranslationTest() {}
}