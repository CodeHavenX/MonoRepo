package com.cramsan.flyerboard.server.datastore

import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.framework.annotations.BackendDatastore

/**
 * Interface defining user-related data operations.
 */
@BackendDatastore
interface UserDatastore {
    /**
     * Creates a new user with the given first and last name.
     *
     * @param userId The Supabase Auth UUID of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @return A [Result] containing the created [User] or an error if the operation failed.
     */
    suspend fun createUser(
        userId: UserId,
        firstName: String,
        lastName: String,
    ): Result<User>
}
