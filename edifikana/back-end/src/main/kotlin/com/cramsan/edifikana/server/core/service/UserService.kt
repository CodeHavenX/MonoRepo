package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.repository.UserDatabase
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateUserRequest
import com.cramsan.edifikana.server.core.service.password.PasswordGenerator

/**
 * Service for user operations.
 */
class UserService(
    private val userDatabase: UserDatabase,
    private val passwordGenerator: PasswordGenerator,
) {

    /**
     * Creates a user with the provided [email].
     */
    suspend fun createUser(
        email: String,
    ): User {
        val password = passwordGenerator.generate()

        return userDatabase.createUser(
            request = CreateUserRequest(
                email = email,
                password = password,
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
     * Updates a user with the provided [id] and [email].
     */
    suspend fun updateUser(
        id: UserId,
        email: String?,
    ): User {
        return userDatabase.updateUser(
            request = UpdateUserRequest(
                id = id,
                email = email,
            ),
        ).getOrThrow()
    }

    /**
     * Deletes a user with the provided [id].
     */
    suspend fun deleteUser(
        id: UserId,
    ): Boolean {
        return userDatabase.deleteUser(
            request = DeleteUserRequest(
                id = id,
            )
        ).getOrThrow()
    }
}
