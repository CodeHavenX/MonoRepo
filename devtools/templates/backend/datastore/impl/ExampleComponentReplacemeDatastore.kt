package com.cramsan.templatereplaceme.server.datastore.impl

import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.templatereplaceme.lib.model.ComponentReplacemeId
import com.cramsan.templatereplaceme.server.datastore.ComponentReplacemeDatastore
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceme

/**
 * In-memory implementation of [ComponentReplacemeDatastore].
 *
 * Use this as a starting point and for unit testing. Replace it with a real persistence
 * implementation (e.g. Supabase, Room/SQLite) before deploying to production.
 *
 * Typical replacement pattern:
 * ```
 * class SupabaseComponentReplacemeDatastore(
 *     private val supabaseClient: SupabaseClient,
 * ) : ComponentReplacemeDatastore {
 *     override suspend fun create(id: String): Result<ComponentReplaceme> =
 *         runSuspendCatching(TAG) {
 *             val row = supabaseClient.from("componentreplaceme").insert(mapOf("id" to id)) { ... }
 *             ComponentReplaceme(id = ComponentReplacemeId(row.id))
 *         }
 *     companion object { private const val TAG = "SupabaseComponentReplacemeDatastore" }
 * }
 * ```
 *
 * TODO: Replace this class with a real persistence implementation when ready.
 */
@BackendDatastore
class ExampleComponentReplacemeDatastore : ComponentReplacemeDatastore {
    /**
     * Creates an in-memory [ComponentReplaceme] entity with the given [id].
     *
     * TODO: Replace with real persistence logic.
     */
    override suspend fun create(id: String): Result<ComponentReplaceme> =
        runSuspendCatching(TAG) {
            ComponentReplaceme(id = ComponentReplacemeId(id))
        }

    companion object {
        private const val TAG = "ExampleComponentReplacemeDatastore"
    }
}
