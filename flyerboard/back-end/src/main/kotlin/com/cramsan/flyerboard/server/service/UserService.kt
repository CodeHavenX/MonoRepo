package com.cramsan.flyerboard.server.service

import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.server.datastore.UserDatastore
import com.cramsan.flyerboard.server.datastore.UserProfileDatastore
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.framework.annotations.BackendService
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI

/**
 * Service to manage users.
 */
@BackendService
class UserService(private val userDatastore: UserDatastore, private val userProfileDatastore: UserProfileDatastore) {
    /**
     * Creates a new user with the given first and last name.
     *
     * @param userId The Supabase Auth UUID of the authenticated caller.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @return The created [User].
     */
    suspend fun createUser(
        userId: UserId,
        firstName: String,
        lastName: String,
    ): User {
        logD(TAG, "createUser")
        val user =
            userDatastore
                .createUser(
                    userId,
                    firstName,
                    lastName,
                ).getOrThrow()
        logI(TAG, "User created: $user")

        val userProfile = userProfileDatastore.createUserProfile(userId, UserRole.USER).getOrThrow()
        logI(TAG, "User profile created: $userProfile")
        return user
    }

    /**
     * Retrieves the user identified by [userId].
     *
     * @param userId The Supabase Auth UUID of the user.
     * @return The [User].
     */
    suspend fun getUser(userId: UserId): User {
        logD(TAG, "getUser")
        return userDatastore.getUser(userId).getOrThrow()
    }

    companion object {
        private const val TAG = "UserService"
    }
}
