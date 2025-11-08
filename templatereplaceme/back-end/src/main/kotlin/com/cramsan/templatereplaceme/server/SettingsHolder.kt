package com.cramsan.templatereplaceme.server

import com.cramsan.framework.configuration.ConfigurationMultiplexer
import com.cramsan.framework.configuration.PropertyValue
import com.cramsan.framework.configuration.PropertyValueType

/**
 * Class to read settings from the ConfigurationMultiplexer.
 */
class SettingsHolder(
    private val configuration: ConfigurationMultiplexer
) {

    /**
     * Reads a property value from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The property value, or null if not found.
     */
    fun getValue(propertyKey: PropertyKey): PropertyValue? {
        return configuration.readProperty(
            propertyKey.key,
            propertyKey.type,
        )
    }

    /**
     * Reads a string property from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The string value, or null if not found or not a string.
     */
    fun getString(propertyKey: PropertyKey): String? {
        val value = getValue(propertyKey)
        return if (value is PropertyValue.StringValue) {
            value.string
        } else {
            null
        }
    }

    /**
     * Reads a boolean property from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The boolean value, or null if not found or not a boolean.
     */
    fun getBoolean(propertyKey: PropertyKey): Boolean? {
        val value = getValue(propertyKey)
        return if (value is PropertyValue.BooleanType) {
            value.boolean
        } else {
            null
        }
    }

    /**
     * Reads an integer property from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The integer value, or null if not found or not an integer.
     */
    fun getInt(propertyKey: PropertyKey): Int? {
        val value = getValue(propertyKey)
        return if (value is PropertyValue.IntValue) {
            value.integer
        } else {
            null
        }
    }

    /**
     * Reads a long property from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The long value, or null if not found or not a long.
     */
    fun getLong(propertyKey: PropertyKey): Long? {
        val value = getValue(propertyKey)
        return if (value is PropertyValue.LongValue) {
            value.long
        } else {
            null
        }
    }

    /**
     * Gets the property key as resolved by all Configurations.
     *
     * @param propertyKey The key of the property.
     * @return A list of keys.
     */
    fun getKeyNames(propertyKey: PropertyKey): List<String> {
        return configuration.getSearchLocations(propertyKey.key)
    }
}

/**
 * Enum class representing the keys for various properties.
 */
enum class PropertyKey(val key: String, val type: PropertyValueType) {
    LOGGING_LEVEL("logging.level", PropertyValueType.StringType),
    ENABLE_FILE_LOGGING("logging.enable_file_logging", PropertyValueType.BooleanType),
    HALT_ON_FAILURE("halt_on_failure", PropertyValueType.BooleanType),
    ALLOWED_HOST("allowed.host", PropertyValueType.StringType),
}
