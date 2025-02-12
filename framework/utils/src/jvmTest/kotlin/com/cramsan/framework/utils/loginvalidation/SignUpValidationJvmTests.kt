package com.cramsan.framework.utils.loginvalidation

import kotlin.test.Test
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.junit.jupiter.params.provider.CsvSource

/**
 * Unit Tests for the SignUpValidation class.
 */
class SignUpValidationJvmTests {
    /**
     * Test that the [validateEmail] function returns a list of error messages when the username is invalid
     */
    @ParameterizedTest
    @CsvFileSource(resources = ["/loginvalidation/validateEmailNegativeCases.csv"], numLinesToSkip = 1)
    fun validateEmail_for_negative_use_cases_returns_list(email: String, expectedMsg: String) {
        // Act
        val result = validateEmail(email)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains(expectedMsg) }
    }

    /**
     * Test that the [validateEmail] function returns an empty list when the username is valid
     */
    @ParameterizedTest
    @CsvSource(
        "test@gmail.com",
        "another787@test.com",
        "test-1234@hotmail.com",
        "random@me.com",
        "fir.las323@random.com"
    )
    fun validateEmail_return_empty_list_with_valid_email(email: String) {
        // Act
        val result = validateEmail(email)
        // Assert
        assertTrue(result.isEmpty())
    }

    /**
     * Test that the [validatePhoneNumber] function returns a list of error messages when the phone number is invalid
     */
    @ParameterizedTest
    @CsvSource(
        "123456789, Invalid phone number format.",
        "12345678901, Invalid phone number format.",
        "'', Phone number cannot be empty."
    )
    fun validatePhoneNumber_for_negative_use_cases_returns_list(phoneNumber: String, expectedMsg: String) {
        // Act
        val result = validatePhoneNumber(phoneNumber)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains(expectedMsg) }
    }

    /**
     * Validate the [validatePhoneNumber] function returns an empty list when the phone number is valid
     */
    @ParameterizedTest
    @CsvSource(
        "234-567-8901",
        "5017684352",
        "(543) 123-4567"
    )
    fun validatePhoneNumber_has_valid_phone_number_and_returns_empty_list(phoneNumber: String) {
        // Act
        val result = validatePhoneNumber(phoneNumber)
        // Assert
        assertTrue(result.isEmpty())
    }

    /**
     * Validate the [validateName] function returns a list of error messages when the first name or last name is empty
     */
    @ParameterizedTest
    @CsvSource("'', Jones, First name cannot be empty.",
        "Mary,'', Last name cannot be empty.",
        "'','', 'First name cannot be empty., Last name cannot be empty.'")
    fun validateName_has_blank_field_and_returns_a_list(firstName:String, lastName:String, expectedMsg: String) {
        // Act
        val result = validateName(firstName, lastName)
        val expectedError = expectedMsg.split(", ").map { it.trim() }
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.containsAll(expectedError) }
    }

    /**
     * Validate the [validateName] function returns an empty list when the first name and last name are valid
     */
    @Test
    fun validateName_has_valid_first_and_last_name_and_returns_empty_list() {
        // Arrange
        val firstName = "John"
        val lastName = "Doe"
        // Act
        val result = validateName(firstName, lastName)
        // Assert
        assertTrue(result.isEmpty())
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/loginvalidation/validatePassword.csv"], numLinesToSkip = 1)
    fun validaPassword_negative_uses_cases_returns_list_of_errors(password: String, expectedSize: String, expectedMsg: String) {
        // Act
        val result = validatePassword(password)
        val expectedErrors = expectedMsg.split(", ").map { it.trim('\'') }

        // Assert
        assertTrue(result.isNotEmpty())
        assertEquals(expectedSize.toInt(), result.size)
        assertEquals(expectedErrors, result)
    }
}