package com.cramsan.templatereplaceme.server.datastore.impl

import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.templatereplaceme.lib.model.ComponentReplaceMeId
import com.cramsan.templatereplaceme.server.datastore.ComponentReplaceMeDatastore
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceMe

/**
 * In-memory implementation of [ComponentReplaceMeDatastore].
 *
 * Use this as a starting point and for unit testing. Replace it with a real persistence
 * implementation (e.g. Supabase, Room/SQLite) before deploying to production.
 *
 * Typical replacement pattern:
 * ```
 * class SupabaseComponentReplaceMeDatastore(
 *     private val supabaseClient: SupabaseClient,
 * ) : ComponentReplaceMeDatastore {
 *     override suspend fun create(id: String): Result<ComponentReplaceMe> =
 *         runSuspendCatching(TAG) {
 *             val row = supabaseClient.from("componentreplaceme").insert(mapOf("id" to id)) { ... }
 *             ComponentReplaceMe(id = ComponentReplaceMeId(row.id))
 *         }
 *     companion object { private const val TAG = "SupabaseComponentReplaceMeDatastore" }
 * }
 * ```
 *
 * TODO: Replace this class with a real persistence implementation when ready.
 */
@BackendDatastore
class ExampleComponentReplaceMeDatastore : ComponentReplaceMeDatastore {
    /**
     * Creates an in-memory [ComponentReplaceMe] entity with the given [id].
     *
     * TODO: Replace with real persistence logic.
     */
    override suspend fun create(id: String): Result<ComponentReplaceMe> =
        runSuspendCatching(TAG) {
            ComponentReplaceMe(id = ComponentReplaceMeId(id))
        }

    companion object {
        private const val TAG = "ExampleComponentReplaceMeDatastore"
    }
}
