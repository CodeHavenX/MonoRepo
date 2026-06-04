package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.templatereplaceme.client.lib.models.ComponentReplacemeModel
import com.cramsan.templatereplaceme.lib.model.ComponentReplacemeId
import com.cramsan.templatereplaceme.lib.model.network.ComponentReplacemeNetworkResponse

/**
 * Maps a [ComponentReplacemeNetworkResponse] network model to a [ComponentReplacemeModel] domain model.
 */
fun ComponentReplacemeNetworkResponse.toComponentReplacemeModel(): ComponentReplacemeModel {
    return ComponentReplacemeModel(id = ComponentReplacemeId(this.id))
}
