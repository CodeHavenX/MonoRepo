package com.cramsan.templatereplaceme.server.datastore.impl

import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.templatereplaceme.lib.model.ComponentReplacemeId
import com.cramsan.templatereplaceme.server.datastore.ComponentReplacemeDatastore
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceme

/**
 * In-memory implementation of [ComponentReplacemeDatastore].
 *
 * Use this as a starting point or for testing. Replace with a real persistence
 * implementation (e.g., Supabase, SQLite) before deploying to production.
 */
@BackendDatastore
class ExampleComponentReplacemeDatastore : ComponentReplacemeDatastore {
    /**
     * Creates an in-memory [ComponentReplaceme] entity with the given [id].
     */
    override suspend fun create(id: String): Result<ComponentReplaceme> =
        runSuspendCatching(TAG) {
            ComponentReplaceme(id = ComponentReplacemeId(id))
        }

    companion object {
        private const val TAG = "ExampleComponentReplacemeDatastore"
    }
}
