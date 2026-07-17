package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for updating a flyer.
 *
 * [upload] is non-null if and only if the request had `request_upload = true`, and contains a
 * fresh signed upload URL the client should use to upload the flyer's asset.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Response payload after updating a flyer.")
data class UpdateFlyerNetworkResponse(
    @SerialName("flyer")
    @JsonSchema.Description("The updated flyer.")
    val flyer: FlyerNetworkResponse,
    @SerialName("upload")
    @JsonSchema.Description(
        "Signed upload URL for the flyer's asset, present only if the request set request_upload to true.",
    )
    val upload: SignedUploadNetworkResponse?,
) : ResponseBody
