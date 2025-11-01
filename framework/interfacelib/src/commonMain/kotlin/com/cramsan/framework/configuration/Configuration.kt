package com.cramsan.framework.configuration

/**
 * This module has a simple API to easily configure data in local storage. It was designed to be used for simple
 * operations that do not require high performance.
 */
interface Configuration {
    /**
     * Read [key] as nullable [String]
     */
    fun readString(key: String): String?

    /**
     * Read [key] as a nullable [Int]
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

    /**
     * Transforms the key to a different format if needed.
     */
    fun transformKey(key: String): String {
        return key
    }
}
