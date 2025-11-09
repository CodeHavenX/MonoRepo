package com.cramsan.templatereplaceme.client.lib.settings

import com.cramsan.framework.configuration.ConfigurationMultiplexer
import com.cramsan.framework.configuration.PropertyValue
import com.cramsan.framework.configuration.PropertyValueType
import com.cramsan.framework.preferences.Preferences

/**
 * Class to read and write settings.
 */
class SettingsHolder(
    private val preferences: Preferences
) {

    /**
     * Reads a property value from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The property value, or null if not found.
     */
    fun getValue(propertyKey: SettingKey<*>): PropertyValue? {
        return when (propertyKey.type) {
            is PropertyValueType.StringType -> {
                val value = preferences.loadString(propertyKey.key) ?: return null
                PropertyValue.StringValue(value)
            }

            is PropertyValueType.IntType -> {
                val value = preferences.loadInt(propertyKey.key) ?: return null
                PropertyValue.IntValue(value)
            }

            is PropertyValueType.LongType -> {
                val value = preferences.loadLong(propertyKey.key) ?: return null
                PropertyValue.LongValue(value)
            }

            is PropertyValueType.BooleanType -> {
                val value = preferences.loadBoolean(propertyKey.key) ?: return null
                PropertyValue.BooleanType(value)
            }
        }
    }

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

    fun saveString(propertyKey: SettingKey<PropertyValueType.StringType>, value: String?) {
        preferences.saveString(propertyKey.key, value)
    }

    /**
     * Reads a boolean property from the configuration.
     *
     * @param propertyKey The key of the property to read.
     * @return The boolean value, or null if not found or not a boolean.
     */
    fun getBoolean(propertyKey: SettingKey<PropertyValueType.BooleanType>): Boolean? {
        val value = getValue(propertyKey) ?: return null
        return if (value is PropertyValue.BooleanType) {
            value.boolean
        } else {
            error("Expected Boolean value for key ${propertyKey.key}, found: $value")
        }
    }

    fun saveBoolean(propertyKey: SettingKey<PropertyValueType.BooleanType>, value: Boolean) {
        preferences.saveBoolean(propertyKey.key, value)
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

    fun saveInt(propertyKey: SettingKey<PropertyValueType.IntType>, value: Int) {
        preferences.saveInt(propertyKey.key, value)
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

    fun saveLong(propertyKey: SettingKey<PropertyValueType.LongType>, value: Long) {
        preferences.saveLong(propertyKey.key, value)
    }

    fun cleanPreference(key: SettingKey<*>) {
        preferences.remove(key.key)
    }

    fun clearAllPreferences() {
        preferences.clear()
    }
}

data class SettingKey <T : PropertyValueType> (
    val key: String,
    val type: T,
) {
    companion object {
        fun string(key: String) = SettingKey(key, PropertyValueType.StringType)
        fun int(key: String) = SettingKey(key, PropertyValueType.IntType)
        fun long(key: String) = SettingKey(key, PropertyValueType.LongType)
        fun boolean(key: String) = SettingKey(key, PropertyValueType.BooleanType)
    }
}
