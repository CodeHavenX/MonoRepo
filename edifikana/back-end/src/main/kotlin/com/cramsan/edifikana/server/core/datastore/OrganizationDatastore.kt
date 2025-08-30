package com.cramsan.edifikana.server.core.datastore

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
}
