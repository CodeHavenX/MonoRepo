package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.datastore.PropertyDatastore
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeletePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyListsRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePropertyRequest
import com.cramsan.framework.logging.logD

/**
 * Service for property operations.
 */
class PropertyService(
    private val propertyDatastore: PropertyDatastore,
) {

    /**
     * Creates a property with the provided [name].
     */
    suspend fun createProperty(
        name: String,
        address: String,
        organizationId: OrganizationId,
        clientContext: ClientContext.AuthenticatedClientContext,
    ): Property {
        logD(TAG, "createProperty")
        return propertyDatastore.createProperty(
            request = CreatePropertyRequest(
                name = name,
                address = address,
                creatorUserId = clientContext.userId,
                organizationId = organizationId,
            ),
        ).getOrThrow()
    }

    /**
     * Retrieves a property with the provided [id].
     */
    suspend fun getProperty(
        id: PropertyId,
    ): Property? {
        logD(TAG, "getProperty")
        val property = propertyDatastore.getProperty(
            request = GetPropertyRequest(
                propertyId = id,
            ),
        ).getOrNull()

        return property
    }

    /**
     * Retrieves all properties.
     */
    suspend fun getProperties(
        userId: UserId,
    ): List<Property> {
        logD(TAG, "getProperties")
        val properties = propertyDatastore.getProperties(
            GetPropertyListsRequest(
                userId,
            )
        ).getOrThrow()
        return properties
    }

    /**
     * Updates a property with the provided [id] and [name].
     */
    suspend fun updateProperty(
        id: PropertyId,
        name: String?,
    ): Property {
        logD(TAG, "updateProperty")
        return propertyDatastore.updateProperty(
            request = UpdatePropertyRequest(
                propertyId = id,
                name = name,
            ),
        ).getOrThrow()
    }

    /**
     * Deletes a property with the provided [id].
     */
    suspend fun deleteProperty(
        id: PropertyId,
    ): Boolean {
        logD(TAG, "deleteProperty")
        return propertyDatastore.deleteProperty(
            request = DeletePropertyRequest(
                propertyId = id,
            )
        ).getOrThrow()
    }

    companion object {
        private const val TAG = "PropertyService"
    }
}
