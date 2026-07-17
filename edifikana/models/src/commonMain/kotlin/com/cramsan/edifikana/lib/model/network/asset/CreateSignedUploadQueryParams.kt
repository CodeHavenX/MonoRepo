package com.cramsan.edifikana.lib.model.network.asset

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for creating a signed upload URL.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters for creating a signed upload URL for a new asset.")
data class CreateSignedUploadQueryParams(
    @SerialName("filename")
    @JsonSchema.Description("Filename of the asset to upload.")
    val filename: String,
    @SerialName("bucket_id")
    @JsonSchema.Description("Storage bucket the asset will be uploaded to.")
    val bucketId: String,
    @SerialName("resource_id")
    @JsonSchema.Description("Identifier of the resource (e.g. property, task) the asset belongs to.")
    val resourceId: String,
) : QueryParam
