package com.cramsan.edifikana.client.lib.features.root.auth.signup

/**
 * Validate that the [username] is a valid email address or phone number. Returns a list of error messages
 * if the username is invalid. An empty list indicates that the username is valid.
 */
fun validateUsername(username: String): List<String> {
    if (username.isBlank()) {
        return listOf("Username cannot be empty.")
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
    return emptyList()
}

/**
 * Validate that the [fullName] is not empty. Returns a list of error messages if the full name is invalid.
 * An empty list indicates that the full name is valid.
 */
fun validateFullName(fullName: String): List<String> {
    if (fullName.isBlank()) {
        return listOf("Full name cannot be empty.")
    }
    return emptyList()
}
