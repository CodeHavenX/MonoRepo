package com.cramsan.framework.utils.password

import kotlin.random.Random

private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
private const val DIGITS = "0123456789"
private const val SYMBOLS = "!@#\$%^&*()-_=+[]{}|;:,.<>?/"

/**
 * Generates a random password with the specified parameters.
 *
 * @param length The length of the password to generate. Default is 12.
 * @param random The random number generator to use. Default is [Random.Default].
 * @param includeUppercase Whether to include uppercase letters in the password. Default is true.
 * @param includeLowercase Whether to include lowercase letters in the password. Default is true.
 * @param includeDigits Whether to include digits in the password. Default is true.
 * @param includeSymbols Whether to include symbols in the password. Default is true.
 * @return A randomly generated password as a [String].
 */
fun generateRandomPassword(
    length: Int = 12,
    random: Random = Random.Default,
    includeUppercase: Boolean = true,
    includeLowercase: Boolean = true,
    includeDigits: Boolean = true,
    includeSymbols: Boolean = true
): String {
    require(length >= MIN_PASSWORD_LENGTH) { "Password length should be at least 6" }

    Random.nextInt()

    val chars = StringBuilder()
    var requiredParameters = 0
    val charSet = mutableListOf<String>()

    if (includeUppercase) {
        chars.append(UPPERCASE.random(random))
        requiredParameters++
        charSet.add(UPPERCASE)
    }
    if (includeLowercase) {
        chars.append(LOWERCASE.random(random))
        requiredParameters++
        charSet.add(LOWERCASE)
    }
    if (includeDigits) {
        chars.append(DIGITS.random(random))
        requiredParameters++
        charSet.add(DIGITS)
    }
    if (includeSymbols) {
        chars.append(SYMBOLS.random(random))
        requiredParameters++
        charSet.add(SYMBOLS)
    }
    repeat(length - requiredParameters) {
        chars.append(charSet.random(random).random(random))
    }
    return chars.toList().shuffled().joinToString("")
}

private const val MIN_PASSWORD_LENGTH = 6
