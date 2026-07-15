package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for creating a flyer.
 *
 * [upload] contains a signed upload URL the client should use to upload the flyer's asset.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Response payload after creating a flyer, including a signed upload URL for its asset.",
)
data class CreateFlyerNetworkResponse(
    @SerialName("flyer")
    @JsonSchema.Description("The newly created flyer, with PENDING status.")
    val flyer: FlyerNetworkResponse,
    @SerialName("upload")
    @JsonSchema.Description("Signed upload URL the client should use to upload the flyer's asset.")
    val upload: SignedUploadNetworkResponse,
) : ResponseBody
