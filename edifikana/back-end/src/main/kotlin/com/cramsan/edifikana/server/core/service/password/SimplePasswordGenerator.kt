package com.cramsan.edifikana.server.core.service.password

class SimplePasswordGenerator : PasswordGenerator {
    override fun generate(): String {
        return (1..10)
            .map { ALLOWED_CHARS.random() }
            .joinToString("")
    }
}

private val ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#"
