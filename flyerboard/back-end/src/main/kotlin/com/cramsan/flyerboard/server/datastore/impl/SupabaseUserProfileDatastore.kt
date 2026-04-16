package com.cramsan.flyerboard.server.datastore.impl

import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.UserProfileDatastore
import com.cramsan.flyerboard.server.datastore.entity.UserProfileEntity
import com.cramsan.flyerboard.server.datastore.entity.toUserProfile
import com.cramsan.flyerboard.server.service.models.UserProfile
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Supabase implementation of [UserProfileDatastore].
 */
@OptIn(SupabaseModel::class)
class SupabaseUserProfileDatastore(
    private val postgrest: Postgrest,
) : UserProfileDatastore {

    override suspend fun getUserProfile(userId: UserId): Result<UserProfile?> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting user profile: %s", userId)
            postgrest.from(UserProfileEntity.COLLECTION).select {
                filter {
                    UserProfileEntity::id eq userId.userId
                }
            }.decodeSingleOrNull<UserProfileEntity>()?.toUserProfile()
        }

    override suspend fun createUserProfile(userId: UserId, role: UserRole): Result<UserProfile> =
        runSuspendCatching(TAG) {
            logD(TAG, "Creating user profile: %s role=%s", userId, role)
            val entity = UserProfileEntity.CreateUserProfileEntity(
                id = userId.userId,
                role = role.name.lowercase(),
            )
            postgrest.from(UserProfileEntity.COLLECTION).insert(entity) {
                select()
            }.decodeSingle<UserProfileEntity>().toUserProfile()
        }

    override suspend fun updateUserRole(userId: UserId, role: UserRole): Result<UserProfile> =
        runSuspendCatching(TAG) {
            logD(TAG, "Updating role for user: %s to %s", userId, role)
            postgrest.from(UserProfileEntity.COLLECTION).update({
                UserProfileEntity::role setTo role.name.lowercase()
            }) {
                select()
                filter {
                    UserProfileEntity::id eq userId.userId
                }
            }.decodeSingleOrNull<UserProfileEntity>()?.toUserProfile()
                ?: throw ClientRequestExceptions.NotFoundException(
                    "User profile not found: ${userId.userId}",
                )
        }

    companion object {
        private const val TAG = "SupabaseUserProfileDatastore"
    }
}
