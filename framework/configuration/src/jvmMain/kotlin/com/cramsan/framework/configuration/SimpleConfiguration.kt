package com.cramsan.framework.configuration

import java.io.File
import java.util.Properties

/**
 * A simple configuration implementation that reads config properties from a file.
 *
 * @param configFile Absolute or relative path to the `.properties` file. The file must already
 *   exist; if it does not, [IllegalArgumentException] is thrown.
 */
class SimpleConfiguration(private val configFile: String) : Configuration {
    private val properties = Properties()

    init {
        val file = File(configFile)
        require(file.exists()) { "Configuration file not found: $configFile" }
        file.inputStream().use {
            properties.load(it)
        }
    }

    /**
     * Reads a key from the config file.
     */
    override fun readString(key: String): String? {
        return readSimpleConfig(key)
    }

    /**
     * Reads an integer from the config file.
     */
    override fun readInt(key: String): Int? {
        return readSimpleConfig(key)?.toIntOrNull()
    }

    /**
     * Reads a long from the config file.
     */
    override fun readLong(key: String): Long? {
        return readSimpleConfig(key)?.toLongOrNull()
    }

    /**
     * Reads a double from the config file.
     */
    override fun readBoolean(key: String): Boolean? {
        return readSimpleConfig(key)?.toBooleanStrictOrNull()
    }

    /**
     * Reads a property from the config file.
     */
    private fun readSimpleConfig(
        key: String,
    ): String? {
        return properties.getProperty(key)
    }

    override fun transformKey(key: String): String {
        // Transform keys to only contain lowercase letters, numbers, periods and underscores.
        return key
            .map { char ->
                when {
                    char.isLetterOrDigit() -> char.lowercaseChar()
                    char == '.' || char == '_' -> char
                    else -> '_'
                }
            }.joinToString("")
    }
}
