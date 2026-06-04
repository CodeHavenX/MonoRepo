package com.cramsan.templatereplaceme.server.datastore

import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceMe

/**
 * Data access contract for [ComponentReplaceMe] entities.
 *
 * The datastore layer is responsible only for optimised data access — no business logic
 * belongs here. Provide one concrete implementation per persistence backend
 * (e.g. Supabase, SQLite, in-memory for tests).
 *
 * Registration checklist:
 * - TODO: Add a concrete implementation (e.g. `SupabaseComponentReplaceMeDatastore`).
 * - TODO: Add `singleOf(::SupabaseComponentReplaceMeDatastore) { bind<ComponentReplaceMeDatastore>() }`
 *         to DatastoreModule.kt.
 *
 * TODO: Add one suspend function per persistence operation this resource needs, e.g.:
 * ```
 * suspend fun getById(id: String): Result<ComponentReplaceMe>
 * suspend fun update(entity: ComponentReplaceMe): Result<ComponentReplaceMe>
 * suspend fun delete(id: String): Result<Unit>
 * suspend fun listAll(): Result<List<ComponentReplaceMe>>
 * ```
 *
 * @see com.cramsan.templatereplaceme.server.datastore.impl.ExampleComponentReplaceMeDatastore
 *   for the in-memory reference implementation used in tests
 */
@BackendDatastore
interface ComponentReplaceMeDatastore {
    /**
     * Persists a new [ComponentReplaceMe] entity and returns the saved result.
     *
     * @param id The identifier to assign to the new entity.
     * @return A [Result] containing the saved [ComponentReplaceMe] or an error.
     */
    suspend fun create(id: String): Result<ComponentReplaceMe>
}
