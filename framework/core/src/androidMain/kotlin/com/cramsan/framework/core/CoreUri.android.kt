package com.cramsan.framework.core

import android.net.Uri

/**
 * CoreUri is a wrapper around the Android Uri class to provide a common interface for
 * handling URIs across platforms.
 */
actual class CoreUri(
    private val uriString: String,
) {

    // Lazily initialize the Android Uri only when needed
    private val uri: Uri by lazy { Uri.parse(uriString) }

    /**
     * Get the URI as a string.
     */
    actual fun getUri(): String {
        return uriString
    }

    /**
     * Get the URI as an Android Uri object.
     */
    fun getAndroidUri(): Uri {
        return uri
    }

    actual override fun toString(): String {
        return uriString
    }

    actual override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CoreUri) return false

        if (uriString != other.uriString) return false

        return true
    }

    actual override fun hashCode(): Int {
        return uriString.hashCode()
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
