package com.cramsan.framework.configuration

import kotlin.jvm.JvmInline

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
sealed interface PropertyValue {
    /**
     * Represents a String property value.
     */
    @JvmInline
    value class StringValue(val string: String) : PropertyValue

    /**
     * Represents an Int property value.
     */
    @JvmInline
    value class IntValue(val integer: Int) : PropertyValue

    /**
     * Represents a Long property value.
     */
    @JvmInline
    value class LongValue(val long: Long) : PropertyValue

    /**
     * Represents a Boolean property value.
     */
    @JvmInline
    value class BooleanValue(val boolean: Boolean) : PropertyValue
}
