package com.cramsan.flyerboard.server.datastore.impl

import com.cramsan.flyerboard.server.datastore.UserDatastore
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.framework.annotations.BackendDatastore
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Supabase implementation of [UserDatastore].
 */
@BackendDatastore
class SupabaseUserDatastore(private val postgrest: Postgrest) : UserDatastore {
    override suspend fun createUser(
        firstName: String,
        lastName: String,
    ): Result<User> = TODO("Not yet implemented")

    companion object {
        private const val TAG = "SupabaseUserDatastore"
    }
}
