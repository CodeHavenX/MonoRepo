package com.cramsan.framework.configuration

/**
 * Represents the type of a property value.
 */
sealed class PropertyValueType {
    /**
     * Represents a String property value type.
     */
    object StringType : PropertyValueType()

    /**
     * Represents an Int property value type.
     */
    object IntType : PropertyValueType()

    /**
     * Represents a Long property value type.
     */
    object LongType : PropertyValueType()

    /**
     * Represents a Boolean property value type.
     */
    object BooleanType : PropertyValueType()
}

/**
 * Represents a property value.
 */
sealed class PropertyValue {
    /**
     * Represents a String property value.
     */
    data class StringValue(val string: String) : PropertyValue()

    /**
     * Represents an Int property value.
     */
    data class IntValue(val integer: Int) : PropertyValue()

    /**
     * Represents a Long property value.
     */
    data class LongValue(val long: Long) : PropertyValue()

    /**
     * Represents a Boolean property value.
     */
    data class BooleanValue(val boolean: Boolean) : PropertyValue()
}
