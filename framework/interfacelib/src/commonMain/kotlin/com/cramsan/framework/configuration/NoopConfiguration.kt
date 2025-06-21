package com.cramsan.framework.configuration

/**
 * No-op implementation of [Configuration] that always returns null.
 */
class NoopConfiguration : Configuration {
    override fun readString(key: String): String? = null
    override fun readInt(key: String): Int? = null
    override fun readLong(key: String): Long? = null
    override fun readBoolean(key: String): Boolean? = null
}
