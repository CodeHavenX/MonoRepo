package com.cramsan.framework.core

import android.net.Uri

actual class CoreUri(
    private val uri: Uri,
) {

    actual fun getUri(): String {
        return uri.toString()
    }

    fun getAndroidUri(): Uri {
        return uri
    }

    actual override fun toString(): String {
        return uri.toString()
    }

    actual companion object {
        actual fun createUri(uri: String): CoreUri {
            return CoreUri(Uri.parse(uri))
        }
    }
}
