package com.cramsan.flyerboard.server.datastore.impl

import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.UserDatastore
import com.cramsan.flyerboard.server.datastore.entity.UserEntity
import com.cramsan.flyerboard.server.datastore.entity.toUser
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Supabase implementation of [UserDatastore].
 */
@BackendDatastore
class SupabaseUserDatastore(private val postgrest: Postgrest) : UserDatastore {
    override suspend fun createUser(
        userId: UserId,
        firstName: String,
        lastName: String,
    ): Result<User> =
        runSuspendCatching(TAG) {
            logD(TAG, "Creating user: %s", userId)
            val entity =
                UserEntity.CreateUserEntity(
                    id = userId.userId,
                    firstName = firstName,
                    lastName = lastName,
                )
            postgrest
                .from(UserEntity.COLLECTION)
                .insert(entity) {
                    select()
                }.decodeSingle<UserEntity>()
                .toUser()
        }

    companion object {
        private const val TAG = "SupabaseUserDatastore"
    }
}
