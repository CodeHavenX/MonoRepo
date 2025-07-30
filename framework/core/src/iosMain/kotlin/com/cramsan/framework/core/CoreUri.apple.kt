package com.cramsan.framework.core

actual class CoreUri(
    private val uri: String,
) {
    
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
        actual fun createUri(uri: String): CoreUri {
            return CoreUri(uri)
        }
    }
}