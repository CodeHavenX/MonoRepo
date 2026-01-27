package com.cramsan.framework.utils.format

/**
 * Replace Windows line endings with Unix line endings.
 */
fun String.replaceWindowLEWithUnixLE(): String = replace("\r\n", "\n")
