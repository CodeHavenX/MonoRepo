package com.cramsan.edifikana.server.core.datastore

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.Organization

/**
 * Interface for interacting with the organization database.
 */
interface OrganizationDatastore {

    /**
     * Create a new organization.
     *
     * @param owner The ID of the user who will own the organization.
     * @return The created organization or an error.
     */
    suspend fun createOrganization(owner: UserId): Result<Organization>

    /**
     * Retrieve an organization by its ID.
     *
     * @param id The ID of the organization.
     * @return The organization if found, or null if not found, or an error.
     */
    suspend fun getOrganization(id: OrganizationId): Result<Organization?>

    /**
     * Update an existing organization.
     *
     * @param id The ID of the organization to update.
     * @param owner The new owner ID for the organization.
     * @return The updated organization or an error.
     */
    suspend fun updateOrganization(id: OrganizationId, owner: UserId): Result<Organization>

    /**
     * Delete an organization by its ID.
     *
     * @param id The ID of the organization to delete.
     * @return True if deletion was successful, false otherwise, or an error.
     */
    suspend fun deleteOrganization(id: OrganizationId): Result<Boolean>

    /**
     * Retrieve all organizations that a user belongs to.
     *
     * @param userId The ID of the user.
     * @return A list of organizations the user belongs to, or an error.
     */
    suspend fun getOrganizationsForUser(userId: UserId): Result<List<Organization>>

    /**
     * Add a user to an organization.
     *
     * @param userId The ID of the user to add.
     * @param organizationId The ID of the organization to which the user will be added.
     * @return Unit if successful, or an error.
     */
    suspend fun addUserToOrganization(userId: UserId, organizationId: OrganizationId): Result<Unit>

    /**
     * Remove a user from an organization.
     *
     * @param userId The ID of the user to remove.
     * @param organizationId The ID of the organization from which the user will be removed.
     * @return Unit if successful, or an error.
     */
    suspend fun removeUserFromOrganization(userId: UserId, organizationId: OrganizationId): Result<Unit>
}
