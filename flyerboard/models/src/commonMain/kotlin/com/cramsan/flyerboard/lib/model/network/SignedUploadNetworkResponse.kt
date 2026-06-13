package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
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
data class SignedUploadNetworkResponse(
    @SerialName("signed_url")
    val signedUrl: String,
    @SerialName("token")
    val token: String?,
) : ResponseBody
