package com.cramsan.flyerboard.server.datastore

import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.service.models.UserProfile

/**
 * Interface defining data operations for user profiles.
 */
interface UserProfileDatastore {

    /**
     * Retrieves the user profile for [userId]. Returns null if no profile exists yet.
     */
    suspend fun getUserProfile(userId: UserId): Result<UserProfile?>

    /**
     * Inserts a new user profile row for [userId] with the given [role].
     * Typically called at signup time via a Supabase auth trigger or the first authenticated request.
     */
    suspend fun createUserProfile(userId: UserId, role: UserRole): Result<UserProfile>

    /**
     * Updates the [role] of an existing user profile. Returns the updated [UserProfile].
     */
    suspend fun updateUserRole(userId: UserId, role: UserRole): Result<UserProfile>
}
