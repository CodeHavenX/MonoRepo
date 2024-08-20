package com.cramsan.framework.core

/**
 * Interface for a URI object. This URI is safe to use in a cross-platform environment.
 */
actual class CoreUri {

    /**
     * Get the URI as a string.
     */
    actual fun getUri(): String {
        TODO("Not yet implemented")
    }

    actual companion object {

        /**
         * Create a URI object from a string.
         */
        actual fun createUri(uri: String): CoreUri {
            TODO("Not yet implemented")
        }
    }
}
