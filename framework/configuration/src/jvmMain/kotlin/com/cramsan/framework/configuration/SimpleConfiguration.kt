package com.cramsan.framework.configuration

import java.io.File
import java.util.Properties

/**
 * A simple configuration implementation that reads config properties from a file.
 */
class SimpleConfiguration(private val configFile: String) : Configuration{
    /**
     * Reads a key from the config file.
     */
    override fun readKey(key: String): String? {
        return readSimpleConfig(key)
    }

    /**
     * Reads an integer from the config file.
     */
    override fun readInt(key: String): Int? {
        return readSimpleConfig(key)?.toInt()
    }

    /**
     * Reads a long from the config file.
     */
    override fun readLong(key: String): Long? {
        return readSimpleConfig(key)?.toLong()
    }

    /**
     * Reads a double from the config file.
     */
    override fun readBoolean(key: String): Boolean? {
        return readSimpleConfig(key)?.toBoolean()
    }

    /**
     * Reads a property from the config file.
     */
    private fun readSimpleConfig(
        key: String,
    ): String? {
        val properties = Properties()
        File(configFile).inputStream().use {
            properties.load(it)
        }
        return properties.getProperty(key)
    }
}

