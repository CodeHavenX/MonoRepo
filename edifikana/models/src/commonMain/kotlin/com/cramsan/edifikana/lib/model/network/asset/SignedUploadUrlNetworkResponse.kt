package com.cramsan.edifikana.lib.model.network.asset

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response body for a signed upload URL request.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A signed URL the client can use to upload a new asset.")
data class SignedUploadUrlNetworkResponse(
    @SerialName("signed_url")
    @JsonSchema.Description("Signed URL to upload the asset to.")
    @JsonSchema.Format("uri")
    val signedUrl: String,
    @SerialName("path")
    @JsonSchema.Description("Storage path the asset will be stored at.")
    val path: String,
    @SerialName("asset_id")
    @JsonSchema.Description("Identifier assigned to the asset.")
    val assetId: String,
) : ResponseBody
