package com.codehavenx.alpaca.frontend.appcore.service

import com.codehavenx.alpaca.frontend.appcore.models.User

/**
 * Service to perform operations on users.
 */
interface UserService {

    /**
     * Get the list of users.
     */
    suspend fun getUsers(): Result<List<User>>

    /**
     * Create a new user.
     */
    suspend fun createUser(
        userName: String,
        email: String?,
        phoneNumber: String?,
    ): Result<User>
}
