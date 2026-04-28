package com.cramsan.edifikana.lib.model.network.asset

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response body for a signed upload URL request.
 */
@NetworkModel
@Serializable
data class SignedUploadUrlNetworkResponse(
    @SerialName("signed_url") val signedUrl: String,
    @SerialName("path") val path: String,
) : ResponseBody
