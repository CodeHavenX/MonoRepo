package com.cramsan.templatereplaceme.server.service.models

import com.cramsan.templatereplaceme.lib.model.ComponentReplaceMeId

/**
 * Service-layer domain model for a [ComponentReplaceMe] entity.
 *
 * This model is internal to the backend and never crosses the network boundary.
 * Network transfer uses [com.cramsan.templatereplaceme.lib.model.network.ComponentReplaceMeNetworkResponse].
 */
data class ComponentReplaceMe(val id: ComponentReplaceMeId)
