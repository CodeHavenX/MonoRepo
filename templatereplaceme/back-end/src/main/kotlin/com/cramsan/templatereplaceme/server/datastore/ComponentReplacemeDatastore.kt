package com.cramsan.templatereplaceme.server.datastore

import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceme

/**
 * Data access interface for [ComponentReplaceme] entities.
 *
 * The datastore layer is responsible only for optimized data access — no business logic belongs here.
 * Provide one implementation per persistence backend (e.g., Supabase, Room, in-memory).
 *
 * @see com.cramsan.templatereplaceme.server.datastore.impl.ExampleComponentReplacemeDatastore
 *   for the in-memory reference implementation
 */
@BackendDatastore
interface ComponentReplacemeDatastore {
    /**
     * Persists a new [ComponentReplaceme] entity and returns the saved result.
     *
     * @param id The identifier to assign to the new entity.
     * @return A [Result] containing the saved [ComponentReplaceme] or an error.
     */
    suspend fun create(id: String): Result<ComponentReplaceme>
}
