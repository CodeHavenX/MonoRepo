package com.cramsan.framework.utils.loginvalidation

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Unit Tests for the SignUpValidation class.
 */
class SignUpValidationJvmTests {
    /**
     * Test that the [validateUsernameEmail] function returns a list of error messages when the username is invalid
     */
    @ParameterizedTest
    @CsvFileSource(resources = ["/loginvalidation/validateUsernameEmailNegativeCases.csv"], numLinesToSkip = 1)
    fun validateUsernameEmail_for_negative_and_positive_use_cases(username: String, expectedMsg: String) {
        // Act
        val result = validateUsernameEmail(username)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains(expectedMsg) }
    }

    /**
     * Test that the [validateUsernameEmail] function returns an empty list when the username is valid
     */
    @ParameterizedTest
    @CsvSource(
        "test@gmail.com",
        "another787@test.com",
        "test-1234@hotmail.com",
        "random@me.com",
        "fir.las323@random.com"
    )
    fun validateUsernameEmail_return_empty_list_with_valid_email(username: String) {
        // Act
        val result = validateUsernameEmail(username)
        // Assert
        assertTrue(result.isEmpty())
    }

    /**
     * Test that the [validateUsernamePhoneNumber] function returns a list of error messages when the phone number is invalid
     */
    @ParameterizedTest
    @CsvSource(
        "123456789, Username must be a valid phone number.",
        "12345678901, Username must be a valid phone number.",
        "'', Username cannot be empty."
    )
    fun validateUsernamePhoneNumber_for_negative_use_cases_returns_list(phoneNumber: String, expectedMsg: String) {
        // Act
        val result = validateUsernamePhoneNumber(phoneNumber)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains(expectedMsg) }
    }

    /**
     * Validate the [validateUsernamePhoneNumber] function returns an empty list when the phone number is valid
     */
    @ParameterizedTest
    @CsvSource(
        "234-567-8901",
        "5017684352",
        "(543) 123-4567"
    )
    fun validateUsernamePhoneNumber_has_valid_phone_number_and_returns_empty_list(phoneNumber: String) {
        // Act
        val result = validateUsernamePhoneNumber(phoneNumber)
        // Assert
        assertTrue(result.isEmpty())
    }

    /**
     * Validate the [validateName] function returns a list of error messages when the first name or last name is empty
     */
    @ParameterizedTest
    @CsvSource("'', Jones, First name cannot be empty.",
        "Mary,'', Last name cannot be empty.",
        "'','', First name cannot be empty.")
    fun validateName_has_blank_field_and_returns_a_list(firstName:String, lastName:String, expectedMsg: String) {
        // Act
        val result = validateName(firstName, lastName)
        // Assert
        assertTrue(result.isNotEmpty())
        assertTrue { result.contains(expectedMsg) }
    }
}