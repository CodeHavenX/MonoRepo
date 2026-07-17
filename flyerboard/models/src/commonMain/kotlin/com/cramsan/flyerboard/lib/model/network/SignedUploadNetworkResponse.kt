package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response body describing a signed upload URL for a flyer's asset.
 *
 * The client uploads the asset bytes directly to [signedUrl] (the [token] is embedded in the
 * URL's query string).
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A signed upload URL for a flyer's asset.")
data class SignedUploadNetworkResponse(
    @SerialName("signed_url")
    @JsonSchema.Description("URL the client should upload the asset bytes to directly.")
    @JsonSchema.Format("uri")
    val signedUrl: String,
    @SerialName("token")
    @JsonSchema.Description("Upload token embedded in the signed URL's query string, if applicable.")
    val token: String?,
) : ResponseBody
