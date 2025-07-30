package com.cramsan.framework.core

/**
 * Interface for a URI object. This URI is safe to use in a cross-platform environment.
 */
actual class CoreUri(
    private val uri: String,
) {

    /**
     * Get the URI as a string.
     */
    actual fun getUri(): String {
        return uri
    }

    actual override fun toString(): String {
        return uri
    }

    actual override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CoreUri) return false

        if (uri != other.uri) return false

        return true
    }

    actual override fun hashCode(): Int {
        return uri.hashCode()
    }

    actual companion object {

        /**
         * Create a URI object from a string.
         */
        actual fun createUri(uri: String): CoreUri {
            return CoreUri(uri)
        }
    }
}
