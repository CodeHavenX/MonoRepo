package com.cramsan.edifikana.lib.model.network.asset

import com.cramsan.edifikana.lib.model.asset.AssetId
import com.cramsan.edifikana.lib.model.common.Url
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model representing a response for a storage event, which includes
 * the file ID, file name.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A stored file asset, with an optional signed download URL.")
data class AssetNetworkResponse(
    @SerialName("id")
    @JsonSchema.Description("Unique identifier of the asset.")
    val id: AssetId,
    @SerialName("file_name")
    @JsonSchema.Description("Original filename of the asset.")
    val fileName: String,
    @SerialName("signed_url")
    @JsonSchema.Description("Signed URL to download the asset, or null if not available.")
    @JsonSchema.Format("uri")
    val signedUrl: Url?,
) : ResponseBody
