package com.cramsan.framework.configuration

/**
 * This module has a simple API to easily configure data in local storage. It was designed to be used for simple
 * operations that do not require high performance.
 */
interface Configuration {
    /**
     * Read [value] of type [String] as the specified [key]
     */
    fun readKey(key: String): String?

    /**
     * Read [key] as a nullable [String]
     */
    fun readInt(key: String): Int?

    /**
     * Read [key] as a nullable [Long]
     */
    fun readLong(key: String): Long?

    /**
     * Read [key] as a nullable [Boolean]
     */
    fun readBoolean(key: String): Boolean?
}
