package com.cramsan.templatereplaceme.server.datastore

import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceme

/**
 * Data access contract for [ComponentReplaceme] entities.
 *
 * The datastore layer is responsible only for optimised data access — no business logic
 * belongs here. Provide one concrete implementation per persistence backend
 * (e.g. Supabase, SQLite, in-memory for tests).
 *
 * Registration checklist:
 * - TODO: Add a concrete implementation (e.g. `SupabaseComponentReplacemeDatastore`).
 * - TODO: Add `singleOf(::SupabaseComponentReplacemeDatastore) { bind<ComponentReplacemeDatastore>() }`
 *         to DatastoreModule.kt.
 *
 * TODO: Add one suspend function per persistence operation this resource needs, e.g.:
 * ```
 * suspend fun getById(id: String): Result<ComponentReplaceme>
 * suspend fun update(entity: ComponentReplaceme): Result<ComponentReplaceme>
 * suspend fun delete(id: String): Result<Unit>
 * suspend fun listAll(): Result<List<ComponentReplaceme>>
 * ```
 *
 * @see com.cramsan.templatereplaceme.server.datastore.impl.ExampleComponentReplacemeDatastore
 *   for the in-memory reference implementation used in tests
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
