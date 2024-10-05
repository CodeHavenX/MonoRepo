package com.cramsan.edifikana.client.lib.utils

import android.provider.OpenableColumns
import com.cramsan.framework.core.CoreUri

/**
 * Get the filename from the given [CoreUri].
 */
actual fun CoreUri.getFilename(ioDependencies: IODependencies): String {
    ioDependencies.contentResolver.query(this.getAndroidUri(), null, null, null, null)?.use {
            cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()

        return cursor.getString(nameIndex)
    }
    throw RuntimeException("Could not find filename for uri: $this")
}
