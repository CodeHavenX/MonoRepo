package com.codehavenx.alpaca.backend.core.service

import com.codehavenx.alpaca.backend.core.repository.UserDatabase
import com.codehavenx.alpaca.backend.core.service.models.User
import com.codehavenx.alpaca.backend.core.service.models.UserId
import com.codehavenx.alpaca.backend.core.service.models.requests.CreateUserRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.DeleteUserRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.GetUserRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.UpdateUserRequest

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
        val user = userDatabase.getUser(
            request = GetUserRequest(
                id = id,
            ),
        ).getOrNull()

        return user
    }

    /**
     * Retrieves all users.
     */
    suspend fun getUsers(): List<User> {
        val users = userDatabase.getUsers().getOrThrow()
        return users
    }

    /**
     * Updates a user with the provided [id] and [username].
     */
    suspend fun updateUser(
        id: UserId,
        username: String?,
    ): User {
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
