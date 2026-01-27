package com.cramsan.architecture.server.settings

import com.cramsan.framework.configuration.ConfigurationMultiplexer
import com.cramsan.framework.configuration.PropertyValue
import com.cramsan.framework.configuration.PropertyValueType

/**
 * Class to read settings from the ConfigurationMultiplexer.
 */
class SettingsHolder(private val configuration: ConfigurationMultiplexer) {

    /**
     * Reads a property value from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The property value, or null if not found.
     */
    fun getValue(propertyKey: SettingKey<*>): PropertyValue? = configuration.readProperty(
        propertyKey.key,
        propertyKey.type,
    )

    /**
     * Reads a string property from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The string value, or null if not found or not a string.
     */
    fun getString(propertyKey: SettingKey<PropertyValueType.StringType>): String? {
        val value = getValue(propertyKey) ?: return null
        return if (value is PropertyValue.StringValue) {
            value.string
        } else {
            error("Expected String value for key ${propertyKey.key}, found: $value")
        }
    }

    /**
     * Reads a boolean property from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The boolean value, or null if not found or not a boolean.
     */
    fun getBoolean(propertyKey: SettingKey<PropertyValueType.BooleanType>): Boolean? {
        val value = getValue(propertyKey) ?: return null
        return if (value is PropertyValue.BooleanValue) {
            value.boolean
        } else {
            error("Expected Boolean value for key ${propertyKey.key}, found: $value")
        }
    }

    /**
     * Reads an integer property from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The integer value, or null if not found or not an integer.
     */
    fun getInt(propertyKey: SettingKey<PropertyValueType.IntType>): Int? {
        val value = getValue(propertyKey) ?: return null
        return if (value is PropertyValue.IntValue) {
            value.integer
        } else {
            error("Expected Int value for key ${propertyKey.key}, found: $value")
        }
    }

    /**
     * Reads a long property from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The long value, or null if not found or not a long.
     */
    fun getLong(propertyKey: SettingKey<PropertyValueType.LongType>): Long? {
        val value = getValue(propertyKey) ?: return null
        return if (value is PropertyValue.LongValue) {
            value.long
        } else {
            error("Expected Long value for key ${propertyKey.key}, found: $value")
        }
    }

    /**
     * Gets the property key as resolved by all Configurations.
     *
     * @param propertyKey The key of the property.
     * @return A list of keys.
     */
    fun getKeyNames(propertyKey: SettingKey<*>): List<String> = configuration.getSearchLocations(propertyKey.key)
}

/**
 * Represents a key for a setting, along with its expected value type.
 *
 * @param T The type of the property value.
 * @property key The key of the setting.
 * @property type The type of the property value.
 */
data class SettingKey<T : PropertyValueType>(val key: String, val type: T) {
    companion object {
        /**
         * Creates a [SettingKey] for a string property.
         */
        fun string(key: String) = SettingKey(key, PropertyValueType.StringType)

        /**
         * Creates a [SettingKey] for an integer property.
         */
        fun int(key: String) = SettingKey(key, PropertyValueType.IntType)

        /**
         * Creates a [SettingKey] for a long property.
         */
        fun long(key: String) = SettingKey(key, PropertyValueType.LongType)

        /**
         * Creates a [SettingKey] for a boolean property.
         */
        fun boolean(key: String) = SettingKey(key, PropertyValueType.BooleanType)
    }
}
