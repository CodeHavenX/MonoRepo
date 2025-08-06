package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new file in the storage system.
 */
@NetworkModel
@Serializable
data class CreateAssetNetworkRequest(
    @SerialName("file_name")
    val fileName: String,
    @SerialName("content")
    val content: ByteArray,
) {
    /**
     * Checks if this request is equal to another object.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CreateAssetNetworkRequest

        if (fileName != other.fileName) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    /**
     * Generates a hash code for this request.
     */
    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}