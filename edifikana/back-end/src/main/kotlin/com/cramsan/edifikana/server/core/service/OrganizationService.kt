package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.core.service.models.Organization
import com.cramsan.framework.logging.logD

/**
 * Service for organization operations.
 */
class OrganizationService(
    private val organizationDatastore: OrganizationDatastore,
) {

    /**
     * Retrieves an organization with the provided [id].
     */
    suspend fun getOrganization(id: OrganizationId): Organization? {
        logD(TAG, "getOrganization")
        return organizationDatastore.getOrganization(id).getOrNull()
    }

    /**
     * Retrieves all organizations that the user belongs to.
     */
    suspend fun getOrganizations(
        userId: UserId,
    ): List<Organization> {
        logD(TAG, "getOrganizations")
        return organizationDatastore.getOrganizationsForUser(userId).getOrThrow()
    }

    companion object {
        private const val TAG = "OrganizationService"
    }
}
