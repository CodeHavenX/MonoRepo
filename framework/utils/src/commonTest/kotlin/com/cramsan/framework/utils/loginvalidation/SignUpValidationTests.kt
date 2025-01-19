package com.cramsan.framework.utils.loginvalidation

import kotlin.test.Test
import kotlin.test.assertTrue

class SignUpValidationTests {
    @Test
    fun validateUsernameEmail_has_blank_username_and_returns_list() {
        // Arrange
        val username = ""
        // Act
        val result = validateUsernameEmail(username)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Username cannot be empty.") }
    }

    @Test
    fun validateUsernameEmail_has_invalid_email_and_returns_list() {
        // Arrange
        val username = "test"
        // Act
        val result = validateUsernameEmail(username)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Username must be a valid email address.") }
    }

    @Test
    fun validateUsernameEmail_has_valid_email_and_returns_empty_list() {
        // Arrange
        val username = "test@gmail.com"
        // Act
        val result = validateUsernameEmail(username)
        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun validateUsernamePhoneNumber_has_blank_username_and_returns_list() {
        // Arrange
        val username = ""
        // Act
        val result = validateUsernamePhoneNumber(username)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Username cannot be empty.") }
    }

    @Test
    fun validateUsernamePhoneNumber_has_invalid_phone_number_and_returns_list() {
        // Arrange
        val username = "123456789"
        // Act
        val result = validateUsernamePhoneNumber(username)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Username must be a valid phone number.") }
    }



}