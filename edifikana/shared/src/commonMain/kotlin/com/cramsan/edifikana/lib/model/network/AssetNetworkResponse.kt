package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model representing a response for a storage event, which includes
 * the file ID, file name, and the content of the file.
 */
@NetworkModel
@Serializable
data class AssetNetworkResponse (
    @SerialName("id")
    val id: String,
    @SerialName("file_name")
    val fileName: String,
)
