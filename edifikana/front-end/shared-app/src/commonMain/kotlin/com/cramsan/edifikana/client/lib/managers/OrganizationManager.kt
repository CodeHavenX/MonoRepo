package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.Organization
import com.cramsan.edifikana.client.lib.service.OrganizationService
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Manager for organization configuration.
 */
class OrganizationManager(
    private val organizationService: OrganizationService,
    private val dependencies: ManagerDependencies,
) {
    private val activeOrganization: MutableStateFlow<Organization?> = MutableStateFlow(null)

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
     * Set the active organization.
     */
    suspend fun setActiveOrganization(organization: OrganizationId) = dependencies.getOrCatch(TAG) {
        logI(TAG, "setActiveOrganization")
        activeOrganization.value = organizationService.getOrganization(organization).getOrThrow()
    }

    companion object {
        private const val TAG = "OrganizationManager"
    }
}
