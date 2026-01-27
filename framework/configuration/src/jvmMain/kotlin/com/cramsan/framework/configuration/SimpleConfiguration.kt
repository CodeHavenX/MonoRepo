package com.cramsan.framework.configuration

import java.io.File
import java.util.Properties

/**
 * A simple configuration implementation that reads config properties from a file.
 */
class SimpleConfiguration(private val configFile: String) : Configuration {
    // Properties object to store the config properties.
    val properties = Properties()

    // Initializes the configuration by loading the properties from the config file.
    init {
        val file = File(configFile)
        file.createNewFile()
        file.inputStream().use {
            properties.load(it)
        }
    }

    /**
     * Reads a key from the config file.
     */
    override fun readString(key: String): String? = readSimpleConfig(key)

    /**
     * Reads an integer from the config file.
     */
    override fun readInt(key: String): Int? = readSimpleConfig(key)?.toIntOrNull()

    /**
     * Reads a long from the config file.
     */
    override fun readLong(key: String): Long? = readSimpleConfig(key)?.toLongOrNull()

    /**
     * Reads a double from the config file.
     */
    override fun readBoolean(key: String): Boolean? = readSimpleConfig(key)?.toBooleanStrictOrNull()

    /**
     * Reads a property from the config file.
     */
    private fun readSimpleConfig(key: String): String? = properties.getProperty(key)

    override fun transformKey(key: String): String {
        // Transform keys to only contain lowercase letters, numbers, periods and underscores.
        return key.map { char ->
            when {
                char.isLetterOrDigit() -> char.lowercaseChar()
                char == '.' || char == '_' -> char
                else -> '_'
            }
        }.joinToString("")
    }
}
