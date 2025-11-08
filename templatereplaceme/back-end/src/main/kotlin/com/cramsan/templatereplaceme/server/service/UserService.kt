package com.cramsan.templatereplaceme.server.service

import com.cramsan.framework.logging.logD
import com.cramsan.templatereplaceme.server.datastore.UserDatastore
import com.cramsan.templatereplaceme.server.service.models.User

/**
 * Example of service to manage users.
 */
class UserService(
    private val userDatastore: UserDatastore,
) {

    /**
     * Creates a new user with the given first and last name.
     *
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @return A [Result] containing the created [User] or an error if the operation failed.
     */
    suspend fun createUser(
        firstName: String,
        lastName: String,
    ): Result<User> {
        logD(TAG, "createUser")
        return userDatastore.createUser(
            firstName,
            lastName,
        )
    }

    companion object {
        private const val TAG = "UserService"
    }
}
