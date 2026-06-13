package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
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
data class UpdateFlyerNetworkResponse(
    @SerialName("flyer")
    val flyer: FlyerNetworkResponse,
    @SerialName("upload")
    val upload: SignedUploadNetworkResponse?,
) : ResponseBody
