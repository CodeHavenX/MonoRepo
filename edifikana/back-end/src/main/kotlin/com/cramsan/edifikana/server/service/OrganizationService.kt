package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.logging.logD

/**
 * Service for organization operations.
 */
class OrganizationService(private val organizationDatastore: OrganizationDatastore) {

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
    suspend fun getOrganizations(userId: UserId): List<Organization> {
        logD(TAG, "getOrganizations")
        return organizationDatastore.getOrganizationsForUser(userId).getOrThrow()
    }

    /**
     * Creates a new organization with the provided [name] and [description].
     * The user who creates the organization becomes the owner with OWNER role.
     */
    suspend fun createOrganization(userId: UserId, name: String, description: String): Result<Organization> {
        logD(TAG, "createOrganization: %s", name)
        val orgResult = organizationDatastore.createOrganization(name, description)

        if (orgResult.isFailure) {
            logD(TAG, "Failed to create organization: %s", orgResult.exceptionOrNull()?.message)
            return orgResult
        }

        val organization = orgResult.getOrThrow()
        // Add the user as an admin of the organization
        organizationDatastore.addUserToOrganization(userId, organization.id, UserRole.OWNER)
            .onFailure { e ->
                logD(TAG, "Failed to add user to organization: %s", e.message)

                // Attempt to clean up the newly created organization to avoid leaving it orphaned
                organizationDatastore.deleteOrganization(organization.id)
                    .onFailure { cleanupError ->
                        logD(
                            TAG,
                            "Failed to cleanup orphaned organization %s: %s",
                            organization.id,
                            cleanupError.message,
                        )
                    }

                return Result.failure(e.cause ?: RuntimeException("Failed to add user to organization"))
            }

        return orgResult
    }

    /**
     * Updates an organization's name and/or description.
     * Only fields that are non-null will be updated.
     */
    suspend fun updateOrganization(id: OrganizationId, name: String?, description: String?): Result<Organization> {
        logD(TAG, "updateOrganization: %s", id)
        return organizationDatastore.updateOrganization(id, name, description)
    }

    companion object {
        private const val TAG = "OrganizationService"
    }
}
