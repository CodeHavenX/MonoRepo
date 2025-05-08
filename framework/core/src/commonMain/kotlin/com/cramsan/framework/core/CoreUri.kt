package com.cramsan.framework.core

/**
 * Interface for a URI object. This URI is safe to use in a cross-platform environment.
 */
expect class CoreUri {

    /**
     * Get the URI as a string.
     */
    fun getUri(): String

    override fun toString(): String

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    companion object {
        /**
         * Create a URI object from a string.
         */
        fun createUri(uri: String): CoreUri
    }
}
