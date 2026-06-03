package com.cramsan.templatereplaceme.server.service.models

import com.cramsan.templatereplaceme.lib.model.ComponentReplacemeId

/**
 * Service-layer domain model for a [ComponentReplaceme] entity.
 *
 * This model is internal to the backend and never crosses the network boundary.
 * Network transfer uses [com.cramsan.templatereplaceme.lib.model.network.ComponentReplacemeNetworkResponse].
 */
data class ComponentReplaceme(
    val id: ComponentReplacemeId,
)
