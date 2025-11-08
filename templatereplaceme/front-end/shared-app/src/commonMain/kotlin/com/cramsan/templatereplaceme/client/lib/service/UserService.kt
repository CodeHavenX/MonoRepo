package com.cramsan.templatereplaceme.client.lib.service

import com.cramsan.templatereplaceme.client.lib.models.UserModel

/**
 * Service to manage users.
 */
interface UserService {

    /**
     * Create a new user.
     */
    suspend fun createUser(
        firstName: String,
        lastName: String
    ): Result<UserModel>
}
