package com.cramsan.framework.utils.password

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordGeneratorTest {

    @Test
    fun `password has correct length`() {
        val length = 16
        val password = generateRandomPassword(length)
        assertEquals(length, password.length)
    }

    @Test
    fun `password includes all required character types`() {
        val password = generateRandomPassword(20)
        assertTrue(password.any { it.isUpperCase() }, "Should contain uppercase letter")
        assertTrue(password.any { it.isLowerCase() }, "Should contain lowercase letter")
        assertTrue(password.any { it.isDigit() }, "Should contain digit")
        assertTrue(password.any { "!@#\$%^&*()-_=+[]{}|;:,.<>?/".contains(it) }, "Should contain symbol")
    }

    @Test
    fun `password is random`() {
        val password1 = generateRandomPassword(12)
        val password2 = generateRandomPassword(12)
        assertNotEquals(password1, password2)
    }

    @Test
    fun `throws on too short password`() {
        assertFailsWith<IllegalArgumentException> {
            generateRandomPassword(3)
        }
    }

    @Test
    fun `can generate password with only digits`() {
        val password = generateRandomPassword(10, includeUppercase = false, includeLowercase = false, includeDigits = true, includeSymbols = false)
        assertTrue(password.all { it.isDigit() })
    }

    @Test
    fun `can generate password with only symbols`() {
        val password = generateRandomPassword(10, includeUppercase = false, includeLowercase = false, includeDigits = false, includeSymbols = true)
        assertTrue(password.all { "!@#\$%^&*()-_=+[]{}|;:,.<>?/".contains(it) })
    }
}

