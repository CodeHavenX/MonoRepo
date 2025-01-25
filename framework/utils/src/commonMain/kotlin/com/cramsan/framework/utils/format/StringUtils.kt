package com.cramsan.framework.utils.format

/**
 * Replace Windows line endings with Unix line endings.
 */
fun String.replaceWindowLEWithUnixLE(): String {
    return replace("\r\n", "\n")
}
