package com.cramsan.framework.utils.loginvalidation

/**
 * Validate that the [username] is a valid email address. Returns a list of error messages
 * if the username is invalid. An empty list indicates that the username is valid.
 */
fun validateUsernameEmail(username: String): List<String> {
    if (username.isBlank()) {
        return listOf("Username cannot be empty.")
    }
    if (!username.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
        return listOf("Username must be a valid email address.")
    }
    return emptyList()
}

/**
 * Validate that the [username] is a valid email address. Returns a list of error messages
 * if the username is invalid. An empty list indicates that the username is valid.
 * Currently only supports US phone numbers
 */
fun validateUsernamePhoneNumber(phoneNumber: String): List<String> {
    if (phoneNumber.isBlank()) {
        return listOf("Username cannot be empty.")
    }
    // remove spaces, parenthesis, and dashes from phone number
    val username = phoneNumber.replace(Regex("[\\s()-]"), "")

    if (!username.matches(Regex("\\d{10}"))) {
        return listOf("Username must be a valid phone number.")
    }
    return emptyList()
}

/**
 * Validate that the [password] is meet security requirements. Returns a list of error messages if the password
 * is invalid. An empty list indicates that the password is valid.
 */
fun validatePassword(password: String): List<String> {
    if (password.isBlank()) {
        return listOf("Password cannot be empty.")
    }
    if (password.length < 6 && password.length > 24) {
        return listOf("Password must be between 6 and 24 characters long.")
    }
    if (!password.contains(Regex("[A-Z]"))) {
        return listOf("Password must contain at least one uppercase letter.")
    }
    if (!password.contains(Regex("[a-z]"))) {
        return listOf("Password must contain at least one lowercase letter.")
    }
    if (!password.contains(Regex("\\d"))) {
        return listOf("Password must contain at least one number.")
    }
    return emptyList()
}

/**
 * Validate that the [firstName] & [lastName] are not empty. Returns a list of error messages if the full name is invalid.
 * An empty list indicates that the full name is valid.
 */
fun validateName(firstName: String, lastName: String): List<String> {
    if (firstName.isBlank()) {
        return listOf("First name cannot be empty.")
    }
    if (lastName.isBlank()) {
        return listOf("Last name cannot be empty.")
    }
    return emptyList()
}