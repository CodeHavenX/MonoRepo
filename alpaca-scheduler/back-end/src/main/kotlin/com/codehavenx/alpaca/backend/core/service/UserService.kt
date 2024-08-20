package com.codehavenx.alpaca.backend.core.service

import com.codehavenx.alpaca.backend.core.service.models.User
import com.codehavenx.alpaca.backend.core.service.models.UserId
import com.codehavenx.alpaca.backend.core.storage.UserDatabase
import com.codehavenx.alpaca.backend.core.storage.requests.CreateUserRequest
import com.codehavenx.alpaca.backend.core.storage.requests.DeleteUserRequest
import com.codehavenx.alpaca.backend.core.storage.requests.GetUserRequest
import com.codehavenx.alpaca.backend.core.storage.requests.UpdateUserRequest

/**
 * Service for user operations.
 */
class UserService(
    private val userDatabase: UserDatabase,
) {

    /**
     * Creates a user with the provided [username].
     */
    suspend fun createUser(
        username: String,
    ): User {
        return userDatabase.createUser(
            request = CreateUserRequest(
                username = username,
            ),
        ).getOrThrow()
    }

    /**
     * Retrieves a user with the provided [id].
     */
    suspend fun getUser(
        id: UserId,
    ): User? {
        return userDatabase.getUser(
            request = GetUserRequest(
                id = id,
            ),
        ).getOrNull()
    }

    /**
     * Updates a user with the provided [id] and [username].
     */
    suspend fun updateUser(
        id: UserId,
        username: String?,
    ): Boolean {
        return userDatabase.updateUser(
            request = UpdateUserRequest(
                id = id,
                username = username,
            ),
        ).getOrThrow()
    }

    /**
     * Deletes a user with the provided [id].
     */
    suspend fun deleteEvent(
        id: UserId,
    ): Boolean {
        return userDatabase.deleteUser(
            request = DeleteUserRequest(
                id = id,
            )
        ).getOrThrow()
    }
}
