package com.cramsan.edifikana.server.core.service.models

import com.cramsan.edifikana.lib.model.AssetId

/**
 * Domain model representing a file [Asset].
 */
data class Asset(
    val id: AssetId,
    val fileName: String,
    val content: ByteArray,
) {
    /**
     * Checks if this file is equal to another object.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Asset

        if (id != other.id) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    /**
     * Returns a hash code value for this file.
     */
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}
