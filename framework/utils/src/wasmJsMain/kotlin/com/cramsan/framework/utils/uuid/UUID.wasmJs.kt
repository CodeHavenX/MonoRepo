package com.cramsan.framework.utils.uuid

/**
 * Simple UUID generator.
 */
actual object UUID {
    /**
     * Generates a random UUID based on a platform specific implementation.
     */
    actual fun random(): String {
        TODO("Not yet implemented")
    }

    /**
     * Generates a UUID based on the byte content of the provided [input].
     */
    actual fun fromString(input: String): String {
        TODO("Not yet implemented")
    }

}