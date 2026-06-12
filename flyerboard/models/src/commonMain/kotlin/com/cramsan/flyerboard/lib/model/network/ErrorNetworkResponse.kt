package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for an error.
 */
@NetworkModel
@Serializable
data class ErrorNetworkResponse(
    @SerialName("message")
    val message: String,
) : ResponseBody
