package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.templatereplaceme.lib.model.network.ComponentReplacemeNetworkResponse
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceme

/**
 * Maps a [ComponentReplaceme] domain model to a [ComponentReplacemeNetworkResponse] network model.
 */
@NetworkModel
fun ComponentReplaceme.toComponentReplacemeNetworkResponse(): ComponentReplacemeNetworkResponse {
    return ComponentReplacemeNetworkResponse(id = id.id)
}
