package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.AssetId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model representing a response for a storage event, which includes
 * the file ID, file name.
 */
@NetworkModel
@Serializable
data class AssetNetworkResponse(
    @SerialName("id")
    val id: AssetId,
    @SerialName("file_name")
    val fileName: String,
    @SerialName("signed_url")
    val signedUrl: String?,
) : ResponseBody
