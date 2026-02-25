package com.cramsan.edifikana.client.lib.utils

import com.cramsan.framework.core.CoreUri
import java.io.File
import java.net.URI

/**
 * Utility class for CoreUri.
 */
actual fun CoreUri.getFilename(ioDependencies: IODependencies): String {
    val uriString = this.getUri()
    val file = if (uriString.startsWith("file:")) {
        File(URI(uriString))
    } else {
        File(uriString)
    }

    return file.name
}
