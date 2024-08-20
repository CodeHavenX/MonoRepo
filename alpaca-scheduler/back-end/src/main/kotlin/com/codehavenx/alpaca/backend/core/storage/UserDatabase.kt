package com.codehavenx.alpaca.backend.core.storage

import com.codehavenx.alpaca.backend.core.service.models.User
import com.codehavenx.alpaca.backend.core.storage.requests.CreateUserRequest
import com.codehavenx.alpaca.backend.core.storage.requests.DeleteUserRequest
import com.codehavenx.alpaca.backend.core.storage.requests.GetUserRequest
import com.codehavenx.alpaca.backend.core.storage.requests.UpdateUserRequest

/**
 * Interface for interacting with the user database.
 */
interface UserDatabase {
    /**
     * Creates a new user for the given [request]. Returns the [Result] of the operation with the created [User].
     */
    suspend fun createUser(
        request: CreateUserRequest,
    ): Result<User>

    /**
     * Retrieves a user for the given [request]. Returns the [Result] of the operation with the fetched [User] if found.
     */
    suspend fun getUser(
        request: GetUserRequest,
    ): Result<User?>

    /**
     * Updates a user with the given [request]. Returns the [Result] of the operation with the updated [User].
     */
    suspend fun updateUser(
        request: UpdateUserRequest,
    ): Result<Boolean>

    /**
     * Deletes a user with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteUser(
        request: DeleteUserRequest,
    ): Result<Boolean>
}
