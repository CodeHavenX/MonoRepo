package com.cramsan.edifikana.server.core.datastore

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.Organization
import com.cramsan.edifikana.server.core.service.models.requests.CreateOrganizationRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteOrganizationRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetOrganizationRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateOrganizationRequest

/**
 * Interface for interacting with the organization database.
 */
interface OrganizationDatastore {

    /**
     * Create a new organization.
     *
     * @param request The request containing organization details.
     * @return The created organization or an error.
     */
    suspend fun createOrganization(request: CreateOrganizationRequest): Result<Organization>

    /**
     * Retrieve an organization by its ID.
     *
     * @param request The request containing the organization ID.
     * @return The organization if found, or null if not found, or an error.
     */
    suspend fun getOrganization(request: GetOrganizationRequest): Result<Organization?>

    /**
     * Update an existing organization.
     *
     * @param request The request containing updated organization details.
     * @return The updated organization or an error.
     */
    suspend fun updateOrganization(request: UpdateOrganizationRequest): Result<Organization>

    /**
     * Delete an organization by its ID.
     *
     * @param request The request containing the organization ID to delete.
     * @return True if deletion was successful, false otherwise, or an error.
     */
    suspend fun deleteOrganization(request: DeleteOrganizationRequest): Result<Boolean>

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
