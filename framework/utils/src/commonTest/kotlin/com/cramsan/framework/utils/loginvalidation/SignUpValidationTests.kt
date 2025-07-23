package com.cramsan.framework.utils.loginvalidation

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Unit Tests for the SignUpValidation class. Simple tests for multiple platforms that ensure base logic works.
 * See [SignUpValidationJvmTests] for JVM parameterized tests for all test cases.
 */
class SignUpValidationTests {
    /**
     * Test that the [validateEmail] function returns error message when the username is blank
     */
    @Test
    fun validateEmail_has_blank_username_and_returns_list() {
        // Arrange
        val username = ""
        // Act
        val result = validateEmail(username)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Email cannot be empty.") }
    }

    /**
     * Test that the [validateEmail] function returns error message when the username is invalid
     */
    @Test
    fun validateEmail_has_invalid_email_and_returns_list() {
        // Arrange
        val username = "test"
        // Act
        val result = validateEmail(username)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Invalid email format.") }
    }

    /**
     * Test that the [validateEmail] function returns an empty list when the username is valid
     */
    @Test
    fun validateEmail_has_valid_email_and_returns_empty_list() {
        // Arrange
        val username = "test@gmail.com"
        // Act
        val result = validateEmail(username)
        // Assert
        assertTrue(result.isEmpty())
    }

    /**
     * Test that the [validatePhoneNumber] function returns error message when the phone number is blank
     */
    @Test
    fun validatePhoneNumber_has_blank_username_and_returns_list() {
        // Arrange
        val username = ""
        // Act
        val result = validatePhoneNumber(username)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Phone number cannot be empty.") }
    }

    /**
     * Test that the [validatePhoneNumber] function returns error message when the phone number is invalid
     */
    @Test
    fun validatePhoneNumber_has_invalid_phone_number_and_returns_list() {
        // Arrange
        val username = "123456789"
        // Act
        val result = validatePhoneNumber(username)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Invalid phone number format.") }
    }

    /**
     * Validate the [validatePhoneNumber] function returns an empty list when the phone number is valid
     */
    @Test
    fun validatePhoneNumber_has_valid_phone_number_and_returns_empty_list() {
        // Arrange
        val username = "234-567-8901"
        // Act
        val result = validatePhoneNumber(username)
        // Assert
        assertTrue(result.isEmpty())
    }

    /**
     * Test that the [validatePassword] function returns error message when the first name is blank
     */
    @Test
    fun validateName_has_blank_firstName_and_returns_list() {
        // Arrange
        val firstName = ""
        val lastName = "Better"
        // Act
        val result = validateName(firstName, lastName)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("First name cannot be empty.") }
    }

    /**
     * Test that the [validatePassword] function returns error message when the last name is blank
     */
    @Test
    fun validateName_has_blank_lastName_and_returns_list() {
        // Arrange
        val firstName = "Name"
        val lastName = ""
        // Act
        val result = validateName(firstName, lastName)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Last name cannot be empty.") }
    }

    /**
     * Test that the [validatePassword] function returns a list of error messages when the first and last name are blank
     */
    @Test
    fun validateName_has_blank_firstName_and_lastName_and_returns_list() {
        // Arrange
        val firstName = ""
        val lastName = ""
        // Act
        val result = validateName(firstName, lastName)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("First name cannot be empty.") }
        assertTrue { result.contains("Last name cannot be empty.") }
    }

    /**
     * Validate the [validateName] function returns no list of error messages when names filled
     */
    @Test
    fun validateName_has_valid_first_and_last_name_and_returns_empty_list() {
        // Arrange
        val firstName = "Jane"
        val lastName = "Killinger"
        // Act
        val result = validateName(firstName, lastName)
        // Assert
        assertTrue(result.isEmpty())
    }

    /**
     * Test that the [validatePassword] function returns a error message when the password is blank
     */
    @Test
    fun validatePassword_has_blank_password_and_returns_list() {
        // Arrange
        val password = ""
        // Act
        val result = validatePassword(password)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Password cannot be empty.") }
    }

    /**
     * Test that the [validatePassword] function returns a list of error messages when the password is invalid
     */
    @Test
    fun validatePassword_has_invalid_password_and_returns_list() {
        // Arrange
        val password = "pswrd"
        // Act
        val result = validatePassword(password)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Password must be between 6 and 24 characters long.") }
        assertTrue { result.contains("Password must contain at least one uppercase letter.") }
        assertTrue { result.contains("Password must contain at least one symbol.") }
        assertTrue { result.contains("Password must contain at least one number.") }
    }

    /**
     * Test that the [validatePassword] function returns error when password is missing symbols
     */
    @Test
    fun validatePassword_has_invalid_password_missing_symbols_and_returns_error() {
        // Arrange
        val password = "Password1"
        // Act
        val result = validatePassword(password)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains("Password must contain at least one symbol.") }
    }

    /**
     * Validate the [validatePassword] function returns an empty list when the password is valid
     */
    @Test
    fun validatePassword_has_valid_password_and_returns_empty_list() {
        // Arrange
        val password = "Password1!"
        // Act
        val result = validatePassword(password)
        // Assert
        assertTrue(result.isEmpty())
    }

}