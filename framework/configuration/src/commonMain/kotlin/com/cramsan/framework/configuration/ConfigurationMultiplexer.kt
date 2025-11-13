package com.cramsan.framework.configuration

/**
 * A multiplexer for multiple [Configuration] sources.
 *
 * It tries to read properties from each configuration in order until it finds a valid value
 * based on the provided [propertyValueTypePredicate].
 */
class ConfigurationMultiplexer {

    private val configurations = mutableListOf<Configuration>()

    private var propertyValueTypePredicate: ((PropertyValue?) -> Boolean) = DefaultPropertyValueTypePredicate

    /**
     * Sets the list of configurations to read from.
     *
     * @param configs The list of configurations.
     */
    fun setConfigurations(configs: List<Configuration>) {
        configurations.clear()
        configurations.addAll(configs)
    }

    /**
     * Sets the predicate to determine if a property value is valid.
     *
     * @param predicate The predicate function.
     */
    fun setPropertyValueTypePredicate(predicate: (PropertyValue?) -> Boolean) {
        propertyValueTypePredicate = predicate
    }

    /**
     * Reads a property from the configurations.
     *
     * @param key The key of the property to read.
     * @param valueType The type of the property value.
     * @return The property value, or null if not found.
     */

    fun readProperty(key: String, valueType: PropertyValueType): PropertyValue? {
        for (configuration in configurations) {
            val normalizedKey = configuration.transformKey(key)
            val value = when (valueType) {
                is PropertyValueType.StringType -> {
                    val stringValue = configuration.readString(normalizedKey)
                    stringValue?.let { PropertyValue.StringValue(it) }
                }
                is PropertyValueType.IntType -> {
                    val intValue = configuration.readInt(normalizedKey)
                    intValue?.let { PropertyValue.IntValue(it) }
                }
                is PropertyValueType.LongType -> {
                    val longValue = configuration.readLong(normalizedKey)
                    longValue?.let { PropertyValue.LongValue(it) }
                }
                is PropertyValueType.BooleanType -> {
                    val booleanValue = configuration.readBoolean(normalizedKey)
                    booleanValue?.let { PropertyValue.BooleanValue(it) }
                }
            }
            if (propertyValueTypePredicate(value)) {
                return value
            }
        }
        return null
    }

    /**
     * Gets all possible search locations for a given key across all configurations.
     *
     * @param key The key to search for.
     * @return A list of transformed keys for each configuration.
     */
    fun getSearchLocations(key: String): List<String> {
        val locations = mutableListOf<String>()
        for (configuration in configurations) {
            val normalizedKey = configuration.transformKey(key)
            locations.add(normalizedKey)
        }
        return locations
    }

    companion object {
        /**
         * The default predicate to determine if a property value is valid.
         *
         * For String values, it checks if the string is not blank.
         * For Int, Long, and Boolean values, it always returns true.
         */
        val DefaultPropertyValueTypePredicate: (PropertyValue?) -> Boolean = { value ->
            when (value) {
                null -> false
                is PropertyValue.StringValue -> value.string.isNotBlank()
                is PropertyValue.IntValue -> true
                is PropertyValue.LongValue -> true
                is PropertyValue.BooleanValue -> true
            }
        }
    }
}
