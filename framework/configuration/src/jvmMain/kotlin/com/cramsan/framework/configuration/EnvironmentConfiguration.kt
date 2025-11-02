package com.cramsan.framework.configuration

/**
 * A simple configuration implementation that reads from env variables.
 *
 * @param domainPrefix The prefix to use for the environment variables. This IS REQUIRED to be a non-empty
 * alphanumeric string. Only uppercase letters, numbers and underscores are allowed.
 */
class EnvironmentConfiguration(
    private val domainPrefix: String,
) : Configuration {

    init {
        // Ensure that the domain prefix is not empty and uppercase and underscores
        require(domainPrefix.isNotEmpty()) {
            "Domain prefix must be a non-empty string"
        }
        require(
            domainPrefix.all {
                it.isDigit() ||
                    it.isLetter() && it.isUpperCase() ||
                    it == '_'
            }
        ) {
            "Domain prefix must only contain uppercase letters, numbers and underscores"
        }
    }

    /**
     * Reads a string from the environment variables.
     */
    override fun readString(key: String): String? {
        return readSimpleConfig(key)
    }

    /**
     * Reads an integer from the environment variable.
     */
    override fun readInt(key: String): Int? {
        return readSimpleConfig(key)?.toIntOrNull()
    }

    /**
     * Reads a long from the environment variable.
     */
    override fun readLong(key: String): Long? {
        return readSimpleConfig(key)?.toLongOrNull()
    }

    /**
     * Reads a boolean from the environment variable.
     */
    override fun readBoolean(key: String): Boolean? {
        return readSimpleConfig(key)?.toBooleanStrictOrNull()
    }

    /**
     * Reads a property from the environment variable.
     */
    private fun readSimpleConfig(
        key: String,
    ): String? {
        return System.getenv(key)
    }

    /**
     * Transforms the key to match environment variable naming conventions.
     */
    override fun transformKey(key: String): String {
        return key.map {
            // Check if character is alphanumeric
            if (it.isLetterOrDigit()) {
                it.uppercase()
            } else {
                '_'
            }
        }.joinToString("")
    }
}
