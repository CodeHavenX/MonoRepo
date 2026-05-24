package com.cramsan.flyerboard.client.lib.filepicker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Platform-specific file picker. Call [pickFile] from a coroutine to let the user choose a file;
 * returns null when the user cancels or the platform does not support file picking.
 *
 * [backgroundDispatcher] is used for blocking I/O on platforms that require it (JVM). Pass a test
 * dispatcher in unit tests to avoid touching real I/O.
 */
expect class FilePicker(backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default) {
    suspend fun pickFile(): PickedFile?
}

/**
 * A file chosen by the user.
 */
data class PickedFile(val bytes: ByteArray, val name: String, val mimeType: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as PickedFile
        return bytes.contentEquals(other.bytes) && name == other.name && mimeType == other.mimeType
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + mimeType.hashCode()
        return result
    }
}
