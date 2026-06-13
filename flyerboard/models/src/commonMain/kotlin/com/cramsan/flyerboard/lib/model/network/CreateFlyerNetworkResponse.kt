package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for creating a flyer.
 *
 * [upload] contains a signed upload URL the client should use to upload the flyer's asset.
 */
@NetworkModel
@Serializable
data class CreateFlyerNetworkResponse(
    @SerialName("flyer")
    val flyer: FlyerNetworkResponse,
    @SerialName("upload")
    val upload: SignedUploadNetworkResponse,
) : ResponseBody
