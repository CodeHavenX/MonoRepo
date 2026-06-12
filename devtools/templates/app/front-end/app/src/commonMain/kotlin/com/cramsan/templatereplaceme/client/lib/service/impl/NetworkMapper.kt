package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.templatereplaceme.client.lib.models.ComponentReplaceMeModel
import com.cramsan.templatereplaceme.lib.model.ComponentReplaceMeId
import com.cramsan.templatereplaceme.lib.model.network.ComponentReplaceMeNetworkResponse

/**
 * Maps a [ComponentReplaceMeNetworkResponse] network model to a [ComponentReplaceMeModel] domain model.
 */
fun ComponentReplaceMeNetworkResponse.toComponentReplaceMeModel(): ComponentReplaceMeModel {
    return ComponentReplaceMeModel(id = ComponentReplaceMeId(this.id))
}
