package com.cramsan.architecture.client.settings

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
                PropertyValue.BooleanValue(value)
            }
        }
    }

    /**
     * Saves a property value to the configuration.
     *
     * @param propertyKey The key of the property to save.
     * @param value The property value to save.
     */
    fun saveValue(propertyKey: SettingKey<*>, value: PropertyValue) {
        val keyType = propertyKey.type
        when (keyType) {
            is PropertyValueType.StringType -> {
                if (value is PropertyValue.StringValue) {
                    saveString(propertyKey as SettingKey<PropertyValueType.StringType>, value.string)
                } else {
                    error("Expected String value for key ${propertyKey.key}, found: $value")
                }
            }

            is PropertyValueType.IntType -> {
                if (value is PropertyValue.IntValue) {
                    saveInt(propertyKey as SettingKey<PropertyValueType.IntType>, value.integer)
                } else {
                    error("Expected Int value for key ${propertyKey.key}, found: $value")
                }
            }

            is PropertyValueType.LongType -> {
                if (value is PropertyValue.LongValue) {
                    saveLong(propertyKey as SettingKey<PropertyValueType.LongType>, value.long)
                } else {
                    error("Expected Long value for key ${propertyKey.key}, found: $value")
                }
            }

            is PropertyValueType.BooleanType -> {
                if (value is PropertyValue.BooleanValue) {
                    saveBoolean(propertyKey as SettingKey<PropertyValueType.BooleanType>, value.boolean)
                } else {
                    error("Expected Boolean value for key ${propertyKey.key}, found: $value")
                }
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

    /**
     * Saves a string property to the configuration.
     *
     * @param propertyKey The key of the property to save.
     * @param value The string value to save.
     */
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
        return if (value is PropertyValue.BooleanValue) {
            value.boolean
        } else {
            error("Expected Boolean value for key ${propertyKey.key}, found: $value")
        }
    }

    /**
     * Saves a boolean property to the configuration.
     *
     * @param propertyKey The key of the property to save.
     * @param value The boolean value to save.
     */
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

    /**
     * Saves an integer property to the configuration.
     *
     * @param propertyKey The key of the property to save.
     * @param value The integer value to save.
     */
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

    /**
     * Saves a long property to the configuration.
     *
     * @param propertyKey The key of the property to save.
     * @param value The long value to save.
     */
    fun saveLong(propertyKey: SettingKey<PropertyValueType.LongType>, value: Long) {
        preferences.saveLong(propertyKey.key, value)
    }

    /**
     * Cleans a preference by its key.
     *
     * @param key The setting key to remove.
     */
    fun cleanPreference(key: SettingKey<*>) {
        preferences.remove(key.key)
    }

    /**
     * Clears all preferences.
     */
    fun clearAllPreferences() {
        preferences.clear()
    }
}

/**
 * Represents a key for a setting, along with its expected value type.
 *
 * @param T The type of the property value.
 * @property key The key of the setting.
 * @property type The type of the property value.
 */
data class SettingKey<T : PropertyValueType> (
    val key: String,
    val type: T,
) {
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
