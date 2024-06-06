package com.cramsan.framework.core

import android.net.Uri

actual class CoreUri(
    private val uri: Uri,
) {

    constructor(uri: String) : this(Uri.parse(uri))

    actual fun getUri(): String {
        return uri.toString()
    }
}
