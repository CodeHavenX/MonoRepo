package com.cramsan.framework.configuration

import java.io.File
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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
        booleanKey=true
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
    fun `test readString`() {
        val result = configuration.readString("stringKey")
        assertEquals("testValue", result)
    }

    @Test
    fun `test readString with nonExistent key`() {
        val result = configuration.readString("nonExistentKey")
        assertEquals(null, result)
    }

    @ParameterizedTest
    @CsvSource("intKey, 543", "longKey, 1234567890", "booleanKey, true")
    fun `test readString with wrong key type returns string value`(key: String, expected: String) {
        val result = configuration.readString(key)
        assertEquals(expected, result)
    }

    @Test
    fun `test readInt`() {
        val result = configuration.readInt("intKey")
        assertEquals(543, result)
    }

    @Test
    fun `test readInt with nonExistent key`() {
        val result = configuration.readInt("nonExistentKey")
        assertEquals(null, result)
    }

    @ParameterizedTest
    @CsvSource("stringKey", "booleanKey")
    fun `test readInt with wrong key type returns null`(key: String) {
        val result = configuration.readInt(key)
        assertNull(result)
    }

    @Test
    fun `test readLong`() {
        val result = configuration.readLong("longKey")
        assertEquals(1234567890, result)
    }

    @Test
    fun `test readLong with nonExistent key`() {
        val result = configuration.readLong("nonExistentKey")
        assertEquals(null, result)
    }

    @ParameterizedTest
    @CsvSource("stringKey", "booleanKey")
    fun `test readLong with wrong key type returns null`(key: String) {
        val result = configuration.readLong(key)
        assertNull(result)
    }

    @Test
    fun `test readBoolean`() {
        val result = configuration.readBoolean("booleanKey")
        assertTrue(result!!)
    }

    @Test
    fun `test readBoolean with nonExistent key`() {
        val result = configuration.readBoolean("nonExistentKey")
        assertNull(result)
    }

    @ParameterizedTest
    @CsvSource("stringKey", "intKey", "longKey")
    fun `test readBoolean with wrong key type returns null`(key: String) {
        val result = configuration.readBoolean(key)
        assertNull(result)
    }
}
