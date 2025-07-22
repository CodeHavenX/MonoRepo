package com.cramsan.framework.utils.loginvalidation

private const val PASSWORD_MIN_LENGTH = 6
private const val PASSWORD_MAX_LENGTH = 24
// TODO: Update strings to be from resources instead of hardcoded
/**
 * Validate that the [email] and [phoneNumber] are not empty. Returns a list of error messages.
 * Helpful when either email or phone number is required. Keeping for reference.
 */
fun validateUsername(email: String, phoneNumber: String): List<String> {
    return if (email.isNotBlank() && phoneNumber.isBlank()) {
        validateEmail(email)
    } else if (phoneNumber.isNotBlank() && email.isBlank()) {
        validatePhoneNumber(phoneNumber)
    } else if (email.isNotBlank() && phoneNumber.isNotBlank()) {
        validateEmail(email) +
            validatePhoneNumber(phoneNumber)
    } else {
        listOf("An Email or Phone Number is required.")
    }
}

/**
 * Validate that the [email] is a valid email address. Returns a list of error messages
 * if the username is invalid. An empty list indicates that the username is valid.
 */
fun validateEmail(email: String): List<String> {
    if (email.isBlank()) {
        return listOf("Email cannot be empty.")
    }
    if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
        return listOf("Invalid email format.")
    }
    return emptyList()
}

/**
 * Validate that the [phoneNumber] is a valid email address. Returns a list of error messages
 * if the username is invalid. An empty list indicates that the username is valid.
 * Currently only supports US phone numbers
 */
fun validatePhoneNumber(phoneNumber: String): List<String> {
    if (phoneNumber.isBlank()) {
        return listOf("Phone number cannot be empty.")
    }
    // remove spaces, parenthesis, and dashes from phone number
    val username = phoneNumber.replace(Regex("[\\s()-]"), "")

    if (!username.matches(Regex("\\d{10}"))) {
        return listOf("Invalid phone number format.")
    }
    return emptyList()
}

/**
 * Validate that the [password] is meet security requirements. Returns a list of error messages if the password
 * is invalid. An empty list indicates that the password is valid.
 */
fun validatePassword(
    password: String,
    minLength: Int = PASSWORD_MIN_LENGTH,
    maxLength: Int = PASSWORD_MAX_LENGTH,
    includeUppercase: Boolean = true,
    includeLowercase: Boolean = true,
    includeDigits: Boolean = true,
    includeSymbols: Boolean = true
): List<String> {
    val errors = mutableListOf<String>()

    if (password.isBlank()) {
        return listOf("Password cannot be empty.")
    }
    if (minLength > maxLength) {
        errors.add(
            "Invalid password length range: minimum length ($minLength) cannot exceed maximum length ($maxLength)."
        )
    } else if (minLength == maxLength) {
        if (password.length != minLength) {
            errors.add("Password must be exactly $minLength characters long.")
        }
    } else if (password.length !in minLength..maxLength) {
        errors.add("Password must be between $minLength and $maxLength characters long.")
    }
    if (includeUppercase && !password.contains(Regex("[A-Z]"))) {
        errors.add("Password must contain at least one uppercase letter.")
    }
    if (includeLowercase && !password.contains(Regex("[a-z]"))) {
        errors.add("Password must contain at least one lowercase letter.")
    }
    if (includeSymbols && !password.contains(Regex("[!@#\$%^&*(),.?\":{}|<>]"))) {
        errors.add("Password must contain at least one symbol.")
    }
    if (includeDigits && !password.contains(Regex("\\d"))) {
        errors.add("Password must contain at least one number.")
    }
    return errors
}

/**
 * Validate that the [firstName] & [lastName] are not empty. Returns a list of error messages if the full name is invalid.
 * An empty list indicates that the full name is valid.
 */
fun validateName(firstName: String, lastName: String): List<String> {
    val errors = mutableListOf<String>()

    if (firstName.isBlank()) {
        errors.add("First name cannot be empty.")
    }
    if (lastName.isBlank()) {
        errors.add("Last name cannot be empty.")
    }
    return errors
}
