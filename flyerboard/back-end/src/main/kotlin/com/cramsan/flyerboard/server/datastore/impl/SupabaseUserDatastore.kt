package com.cramsan.flyerboard.server.datastore.impl

import com.cramsan.flyerboard.server.datastore.UserDatastore
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.framework.annotations.BackendDatastore

/**
 * Supabase implementation of [UserDatastore].
 */
@BackendDatastore
class SupabaseUserDatastore : UserDatastore {
    override suspend fun createUser(
        firstName: String,
        lastName: String,
    ): Result<User> = TODO("Not yet implemented")
}
