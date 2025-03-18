package com.cramsan.framework.configuration

import java.io.File
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Simple unit tests for ensuring our read actions are returning expected vals from the config file.
 */
class SimpleConfigurationTest {
    private lateinit var configFile: File
    private lateinit var configuration: SimpleConfiguration

    /**
     * Setup the test by creating a temp config file.
     */
    @BeforeEach
    fun setUp() {
        // Create a temp config file for testing
        configFile = File.createTempFile("testConfig", ".properties")
        configFile.writeText(
        """
        stringKey=testValue
        intKey=543
        longKey=1234567890
        boleanKey=true
     """.trimIndent()
        )
        configuration = SimpleConfiguration(configFile.absolutePath)
    }

    /**
     * Tear down the test by deleting the temp config file.
     */
    @AfterEach
    fun tearDown() {
        configFile.delete()
    }

    @Test
    fun `test readKey`() {
        val result = configuration.readKey("stringKey")
        assertEquals("testValue", result)
    }

    @Test
    fun `test readInt`() {
        val result = configuration.readInt("intKey")
        assertEquals(543, result)
    }

    @Test
    fun `test readLong`() {
        val result = configuration.readLong("longKey")
        assertEquals(1234567890, result)
    }

    @Test
    fun `test readBoolean`() {
        val result = configuration.readBoolean("boleanKey")
        assertTrue(result!!)
    }
}