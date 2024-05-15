package com.cramsan.edifikana.client.android.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

fun Uri.getFilename(contentResolver: ContentResolver): String {
    contentResolver.query(this, null, null, null, null)?.use {
            cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()

        return cursor.getString(nameIndex)
    }
    throw RuntimeException("Could not find filename for uri: $this")
}