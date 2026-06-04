package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.templatereplaceme.lib.model.network.ComponentReplaceMeNetworkResponse
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceMe

/**
 * Maps a [ComponentReplaceMe] domain model to a [ComponentReplaceMeNetworkResponse] network model.
 */
@NetworkModel
fun ComponentReplaceMe.toComponentReplaceMeNetworkResponse(): ComponentReplaceMeNetworkResponse {
    return ComponentReplaceMeNetworkResponse(id = id.id)
}
