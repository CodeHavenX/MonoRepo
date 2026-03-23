package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.service.models.User

/**
 * Interface for interacting with the user database.
 */
interface UserDatastore {
    /**
     * Creates a new user for the given [request]. Returns the [Result] of the operation with the created [User].
     */
    suspend fun createUser(
        email: String,
        phoneNumber: String,
        password: String?,
        firstName: String,
        lastName: String,
        isTransient: Boolean,
    ): Result<User>

    /**
     * Associates a user from another service with a new user in our system. Returns the [Result] of the operation with
     * the created [User].
     */
    suspend fun associateUser(
        userId: UserId,
        email: String,
    ): Result<User>

    /**
     * Retrieves a user for the given [request]. Returns the [Result] of the operation with the fetched [User] if found.
     */
    suspend fun getUser(
        id: UserId,
    ): Result<User?>

    /**
     * Retrieves all users. Returns the [Result] of the operation with a list of [User].
     */
    suspend fun getUsers(
        organizationId: OrganizationId,
    ): Result<List<User>>

    /**
     * Retrieves a user by email. Returns the [Result] of the operation with the fetched [User].
     */
    suspend fun getUser(
        email: String,
    ): Result<User?>

    /**
     * Updates a user with the given [request]. Returns the [Result] of the operation with the updated [User].
     */
    suspend fun updateUser(
        id: UserId,
        email: String?,
    ): Result<User>

    /**
     * Deletes a user with the given [request].
     * Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteUser(
        id: UserId,
    ): Result<Boolean>

    /**
     * Permanently deletes a soft-deleted user record by ID.
     * Only purges if the record is already soft-deleted.
     * This is intended for testing and maintenance purposes only.
     * Returns the [Result] of the operation with a [Boolean] indicating if the record was purged.
     */
    suspend fun purgeUser(
        id: UserId,
    ): Result<Boolean>
}
