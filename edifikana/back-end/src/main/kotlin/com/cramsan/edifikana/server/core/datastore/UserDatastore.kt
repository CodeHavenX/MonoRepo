package com.cramsan.edifikana.server.core.datastore

import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.AssociateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePasswordRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateUserRequest

/**
 * Interface for interacting with the user database.
 */
interface UserDatastore {
    /**
     * Creates a new user for the given [request]. Returns the [Result] of the operation with the created [User].
     */
    suspend fun createUser(
        request: CreateUserRequest,
    ): Result<User>

    /**
     * Associates a user from another service with a new user in our system. Returns the [Result] of the operation with
     * the created [User].
     */
    suspend fun associateUser(
        request: AssociateUserRequest,
    ): Result<User>

    /**
     * Retrieves a user for the given [request]. Returns the [Result] of the operation with the fetched [User] if found.
     */
    suspend fun getUser(
        request: GetUserRequest,
    ): Result<User?>

    /**
     * Retrieves all users. Returns the [Result] of the operation with a list of [User].
     */
    suspend fun getUsers(): Result<List<User>>

    /**
     * Updates a user with the given [request]. Returns the [Result] of the operation with the updated [User].
     */
    suspend fun updateUser(
        request: UpdateUserRequest,
    ): Result<User>

    /**
     * Deletes a user with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteUser(
        request: DeleteUserRequest,
    ): Result<Boolean>

    /**
     * Updates the password for a user with the given [request]. Returns the [Result] of the operation with a [Boolean]
     * indicating success.
     */
    suspend fun updatePassword(request: UpdatePasswordRequest): Result<Boolean>
}
