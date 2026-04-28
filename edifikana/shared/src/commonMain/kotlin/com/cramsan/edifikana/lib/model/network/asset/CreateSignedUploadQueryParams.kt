package com.cramsan.edifikana.lib.model.network.asset

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for creating a signed upload URL.
 */
@NetworkModel
@Serializable
data class CreateSignedUploadQueryParams(
    @SerialName("filename") val filename: String,
) : QueryParam
