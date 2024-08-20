package com.codehavenx.alpaca.shared.api.utils

import kotlinx.serialization.Serializable

/**
 * A container object which may or may not contain a non-null value.
 * If a value is present, [isPresent] will return `true` and [get] will return the value.
 */
@Serializable
data class Optional<T> private constructor(private val value: T?) {

    /**
     * Return `true` if there is a value present, otherwise `false`.
     */
    fun isPresent(): Boolean = value != null

    /**
     * Return the non-null value if present, otherwise throw [NoSuchElementException].
     */
    fun get(): T = value ?: throw NoSuchElementException("No value present")

    /**
     * If a value is present, invoke the specified function with the value, otherwise do nothing.
     */
    fun ifPresent(lambda: (T) -> Unit) {
        value?.let(lambda)
    }

    /**
     * Return the value if present, otherwise return `other`.
     */
    fun orElse(other: T): T = value ?: other

    /**
     * Return the value if present, otherwise invoke `other` and return the result of that invocation.
     */
    fun orElseGet(other: () -> T): T = value ?: other()

    companion object {

        /**
         * Returns an [Optional] with the specified present non-null value.
         */
        fun <T> of(value: T): Optional<T> {
            return Optional(value)
        }

        /**
         * Returns an [Optional] with a null value representing that no value was set.
         */
        fun <T> empty(): Optional<T> {
            return Optional(null)
        }
    }
}
