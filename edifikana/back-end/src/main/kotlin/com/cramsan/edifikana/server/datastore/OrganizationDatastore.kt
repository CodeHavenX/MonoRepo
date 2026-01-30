package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.UserRole

/**
 * Interface for interacting with the organization database.
 */
interface OrganizationDatastore {

    /**
     * Create a new organization.
     *
     * @param name The name of the organization.
     * @param description The description of the organization.
     * @return The created organization or an error.
     */
    suspend fun createOrganization(name: String, description: String): Result<Organization>

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
     * @param name The new name for the organization, or null to keep existing.
     * @param description The new description for the organization, or null to keep existing.
     * @return The updated organization or an error.
     */
    suspend fun updateOrganization(
        id: OrganizationId,
        name: String?,
        description: String?
    ): Result<Organization>

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
    suspend fun addUserToOrganization(userId: UserId, organizationId: OrganizationId, role: UserRole): Result<Unit>

    /**
     * Remove a user from an organization.
     *
     * @param userId The ID of the user to remove.
     * @param organizationId The ID of the organization from which the user will be removed.
     * @return Unit if successful, or an error.
     */
    suspend fun removeUserFromOrganization(userId: UserId, organizationId: OrganizationId): Result<Unit>

    /**
     * Get the role of a user within a specific organization.
     *
     * @param userId The ID of the user.
     * @param orgId The ID of the organization.
     * @return The role of the user in the organization, or an error
     */
    suspend fun getUserRole(userId: UserId, orgId: OrganizationId): Result<UserRole?>

    /**
     * Permanently deletes a soft-deleted organization record by ID.
     * Only purges if the record is already soft-deleted.
     * This is intended for testing and maintenance purposes only.
     *
     * @param id The ID of the organization to purge.
     * @return True if the record was purged, false if not found or not soft-deleted.
     */
    suspend fun purgeOrganization(id: OrganizationId): Result<Boolean>
}
