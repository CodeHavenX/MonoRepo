package com.cramsan.edifikana.server.service.password

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

/**
 * Test class for [SimplePasswordGenerator].
 */
class SimplePasswordGeneratorTest {
    private val generator = SimplePasswordGenerator()

    /**
     * Tests that the generate method returns a password of length 10.
     */
    @Test
    fun `generate should return a string of length 10`() {
        val password = generator.generate()
        assertEquals(10, password.length)
    }

    /**
     * Tests that the generate method returns different passwords each time it is called.
     */
    @RepeatedTest(10)
    fun `generate should return different passwords each time`() {
        val password1 = generator.generate()
        val password2 = generator.generate()
        // Not guaranteed, but highly likely for random passwords
        assertTrue(password1 != password2)
    }

    /**
     * Tests that the generated password only contains allowed characters.
     */
    @Test
    fun `generate should only use allowed characters`() {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#"
        val password = generator.generate()
        assertTrue(password.all { it in allowedChars })
    }

    /**
     * Tests that the generate method does not return null or blank strings.
     */
    @Test
    fun `generate should not return null or blank`() {
        val password = generator.generate()
        assertNotNull(password)
        assertTrue(password.isNotBlank())
    }
}
