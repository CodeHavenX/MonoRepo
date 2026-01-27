package com.cramsan.templatereplaceme.server.datastore

import com.cramsan.templatereplaceme.server.service.models.User

/**
 * Interface defining user-related data operations.
 */
interface UserDatastore {

    /**
     * Creates a new user with the given first and last name.
     *
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @return A [Result] containing the created [User] or an error if the operation failed.
     */
    suspend fun createUser(firstName: String, lastName: String): Result<User>
}
