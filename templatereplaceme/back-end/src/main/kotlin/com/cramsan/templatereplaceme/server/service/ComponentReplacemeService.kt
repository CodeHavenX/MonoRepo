package com.cramsan.templatereplaceme.server.service

import com.cramsan.framework.annotations.BackendService
import com.cramsan.framework.logging.logD
import com.cramsan.templatereplaceme.server.datastore.ComponentReplacemeDatastore
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceme

/**
 * Business logic for [ComponentReplaceme] operations.
 *
 * The service layer owns all domain logic and coordinates between the controller
 * (inbound requests) and the datastore (persistence). It must not deal with
 * HTTP concerns or raw data-access details.
 *
 * @see ComponentReplacemeDatastore for persistence operations
 */
@BackendService
class ComponentReplacemeService(
    private val componentreplacemeDatastore: ComponentReplacemeDatastore,
) {
    /**
     * Creates a new [ComponentReplaceme] entity with the given [id].
     *
     * @param id The identifier for the new entity.
     * @return A [Result] containing the created [ComponentReplaceme] or an error.
     */
    suspend fun create(id: String): Result<ComponentReplaceme> {
        logD(TAG, "create")
        return componentreplacemeDatastore.create(id)
    }

    companion object {
        private const val TAG = "ComponentReplacemeService"
    }
}
