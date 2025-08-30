package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.server.core.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.core.service.models.Organization
import com.cramsan.edifikana.server.core.service.models.requests.GetOrganizationRequest
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
        return organizationDatastore.getOrganization(GetOrganizationRequest(id)).getOrNull()
    }

    companion object {
        private const val TAG = "OrganizationService"
    }
}
