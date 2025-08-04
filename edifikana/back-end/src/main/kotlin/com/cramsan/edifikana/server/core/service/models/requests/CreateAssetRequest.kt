package com.cramsan.edifikana.server.core.service.models.requests

/**
 * Domain model representing a request to create a file.
 */
data class CreateAssetRequest (
    val fileName: String,
    val content: ByteArray,
) {
    /**
     * Checks if this CreateFileRequest is equal to another object.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateAssetRequest

        return content.contentEquals(other.content)
    }

    /**
     * Returns a hash code value for this CreateFileRequest.
     */
    override fun hashCode(): Int {
        return content.contentHashCode()
    }
}