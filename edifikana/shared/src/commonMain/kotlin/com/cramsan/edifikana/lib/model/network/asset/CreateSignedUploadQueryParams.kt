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
    @SerialName("bucket_id") val bucketId: String,
    @SerialName("resource_type") val resourceType: StorageResourceType,
    @SerialName("resource_id") val resourceId: String,
) : QueryParam
