package com.cramsan.edifikana.server.core.service.password

/**
 * Simple password generator that generates a random password. Not intended for production use.
 */
class SimplePasswordGenerator : PasswordGenerator {

    @Suppress("MagicNumber")
    override fun generate(): String {
        return (1..10)
            .map { ALLOWED_CHARS.random() }
            .joinToString("")
    }
}

private const val ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#"
