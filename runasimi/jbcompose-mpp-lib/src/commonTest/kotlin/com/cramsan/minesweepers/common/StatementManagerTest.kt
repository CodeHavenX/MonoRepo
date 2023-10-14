package com.cramsan.minesweepers.common

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


abstract class BaseStatementManagerTest {

    lateinit var statementManager: StatementManager


    lateinit var verbProvider: VerbProvider

    abstract fun preGenerateStatementTest()

    @Test
    fun generateStatementTest() {
        preGenerateStatementTest()

        assertEquals("Paykuna tulluyarqanku", statementManager.generateStatement(100).toString())
        assertEquals("Qam chirichinki", statementManager.generateStatement(101).toString())
        assertEquals("Qamkuna tulluyankichik", statementManager.generateStatement(102).toString())
        assertEquals("Ñuqa chirichini", statementManager.generateStatement(103).toString())
    }

    abstract fun preGenerateTranslationTest()

    @Test
    fun generateTranslationTest() {
        preGenerateTranslationTest()

        val statement = statementManager.generateStatement(200)

        assertNotNull(statement)
    }
}