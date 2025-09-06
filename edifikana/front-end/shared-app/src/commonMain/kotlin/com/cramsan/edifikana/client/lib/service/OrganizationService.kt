package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.Organization
import com.cramsan.edifikana.lib.model.OrganizationId
import kotlinx.coroutines.flow.StateFlow

/**
 * Service interface for managing organizations.
 */
interface OrganizationService {

    /**
     * Observable reference to the currently active organization.
     */
    val observableActiveOrganization: StateFlow<Organization?>

    /**
     * Sets the active organization by its ID.
     *
     * @param organizationId The ID of the organization to set as active.
     * @return A [Result] indicating success or failure of the operation.
     */
    suspend fun setActiveOrganization(organizationId: OrganizationId): Result<Unit>

    /**
     * Fetches a single organization by its ID.
     *
     * @param organizationId The ID of the organization to fetch.
     * @return A [Result] containing the [Organization] if successful, or an error if not.
     */
    suspend fun getOrganization(organizationId: OrganizationId): Result<Organization>

    /**
     * Fetches the list of all organizations.
     *
     * @return A [Result] containing a list of [Organization] if successful, or an error if not.
     */
    suspend fun getOrganizations(): Result<List<Organization>>
}
