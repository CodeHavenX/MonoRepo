package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.repository.PropertyDatabase
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeletePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePropertyRequest

/**
 * Service for property operations.
 */
class PropertyService(
    private val propertyDatabase: PropertyDatabase,
) {

    /**
     * Creates a property with the provided [name].
     */
    suspend fun createProperty(
        name: String,
    ): Property {
        return propertyDatabase.createProperty(
            request = CreatePropertyRequest(
                name = name,
            ),
        ).getOrThrow()
    }

    /**
     * Retrieves a property with the provided [id].
     */
    suspend fun getProperty(
        id: PropertyId,
    ): Property? {
        val property = propertyDatabase.getProperty(
            request = GetPropertyRequest(
                propertyId = id,
            ),
        ).getOrNull()

        return property
    }

    /**
     * Retrieves all properties.
     */
    suspend fun getProperties(userId: UserId): List<Property> {
        val properties = propertyDatabase.getProperties(userId).getOrThrow()
        return properties
    }

    /**
     * Updates a property with the provided [id] and [name].
     */
    suspend fun updateProperty(
        id: PropertyId,
        name: String?,
    ): Property {
        return propertyDatabase.updateProperty(
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
        return propertyDatabase.deleteProperty(
            request = DeletePropertyRequest(
                propertyId = id,
            )
        ).getOrThrow()
    }
}
