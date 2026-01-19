package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.Organization
import com.cramsan.edifikana.client.lib.service.OrganizationService
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager for organization configuration.
 */
class OrganizationManager(
    private val organizationService: OrganizationService,
    private val dependencies: ManagerDependencies,
) {

    /**
     * Get a single organization by ID.
     */
    suspend fun getOrganization(organizationId: OrganizationId): Result<Organization> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getOrganization")
        organizationService.getOrganization(organizationId).getOrThrow()
    }

    /**
     * Get the list of organizations.
     */
    suspend fun getOrganizations(): Result<List<Organization>> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getOrganizations")
        organizationService.getOrganizations().getOrThrow()
    }

    /**
     * Create a new organization with the provided name and description.
     */
    suspend fun createOrganization(name: String, description: String): Result<Organization> = dependencies.getOrCatch(
        TAG
    ) {
        logI(TAG, "createOrganization: $name")
        organizationService.createOrganization(name, description).getOrThrow()
    }

    companion object {
        private const val TAG = "OrganizationManager"
    }
}
