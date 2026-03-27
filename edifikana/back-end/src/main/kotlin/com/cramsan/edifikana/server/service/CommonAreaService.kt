package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.CommonAreaType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.datastore.CommonAreaDatastore
import com.cramsan.edifikana.server.service.models.CommonArea
import com.cramsan.framework.logging.logD

/**
 * Service for managing common areas. Delegates persistence to [CommonAreaDatastore].
 */
class CommonAreaService(
    private val commonAreaDatastore: CommonAreaDatastore,
) {

    /**
     * Creates a new common area record.
     */
    suspend fun createCommonArea(
        propertyId: PropertyId,
        name: String,
        type: CommonAreaType,
        description: String?,
    ): CommonArea {
        logD(TAG, "createCommonArea")
        return commonAreaDatastore.createCommonArea(
            propertyId = propertyId,
            name = name,
            type = type,
            description = description,
        ).getOrThrow()
    }

    /**
     * Retrieves a single common area by [commonAreaId]. Returns null if not found.
     */
    suspend fun getCommonArea(commonAreaId: CommonAreaId): CommonArea? {
        logD(TAG, "getCommonArea")
        return commonAreaDatastore.getCommonArea(commonAreaId).getOrNull()
    }

    /**
     * Lists all common areas for the given [propertyId].
     */
    suspend fun getCommonAreasForProperty(propertyId: PropertyId): List<CommonArea> {
        logD(TAG, "getCommonAreasForProperty")
        return commonAreaDatastore.getCommonAreasForProperty(propertyId).getOrThrow()
    }

    /**
     * Updates an existing common area. Returns the updated [CommonArea].
     */
    suspend fun updateCommonArea(
        commonAreaId: CommonAreaId,
        name: String?,
        type: CommonAreaType?,
        description: String?,
    ): CommonArea {
        logD(TAG, "updateCommonArea")
        return commonAreaDatastore.updateCommonArea(
            commonAreaId = commonAreaId,
            name = name,
            type = type,
            description = description,
        ).getOrThrow()
    }

    /**
     * Soft-deletes a common area. Returns true if successfully deleted.
     */
    suspend fun deleteCommonArea(commonAreaId: CommonAreaId): Boolean {
        logD(TAG, "deleteCommonArea")
        return commonAreaDatastore.deleteCommonArea(commonAreaId).getOrThrow()
    }

    companion object {
        private const val TAG = "CommonAreaService"
    }
}
