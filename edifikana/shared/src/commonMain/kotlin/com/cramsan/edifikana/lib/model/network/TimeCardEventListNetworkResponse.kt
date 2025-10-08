package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Response body containing a list of time card events.
 */
@NetworkModel
@Serializable
data class TimeCardEventListNetworkResponse(
    val events: List<TimeCardEventNetworkResponse>,
) : ResponseBody
