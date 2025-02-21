package com.cramsan.edifikana.server.util

import java.io.File
import java.util.Properties

/**
 * Reads a property from the config file.
 */
fun readSimpleProperty(
    key: String,
): String? {
    // TODO: Lets replace this with a more secure way of storing properties.
    val properties = Properties()
    File("config.properties").inputStream().use {
        properties.load(it)
    }
    return properties.getProperty(key)
}
