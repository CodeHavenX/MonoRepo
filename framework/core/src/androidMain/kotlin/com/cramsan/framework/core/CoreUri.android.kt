package com.cramsan.framework.core

import android.net.Uri

/**
 * CoreUri is a wrapper around the Android Uri class to provide a common interface for
 * handling URIs across platforms.
 */
actual class CoreUri(
    private val uri: Uri,
) {

    /**
     * Get the URI as a string.
     */
    actual fun getUri(): String {
        return uri.toString()
    }

    /**
     * Get the URI as an Android Uri object.
     */
    fun getAndroidUri(): Uri {
        return uri
    }

    actual override fun toString(): String {
        return uri.toString()
    }

    actual companion object {

        /**
         * Create a URI object from a string.
         */
        actual fun createUri(uri: String): CoreUri {
            return CoreUri(Uri.parse(uri))
        }
    }
}
