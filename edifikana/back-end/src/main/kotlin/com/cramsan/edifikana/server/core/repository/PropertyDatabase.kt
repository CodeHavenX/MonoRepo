package com.cramsan.edifikana.server.core.repository

import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeletePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyListsRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePropertyRequest

/**
 * Interface for interacting with the property database.
 */
interface PropertyDatabase {

    /**
     * Creates a new property for the given [request]. Returns the [Result] of the operation with the created [Property].
     */
    suspend fun createProperty(
        request: CreatePropertyRequest,
    ): Result<Property>

    /**
     * Retrieves a property for the given [request]. Returns the [Result] of the operation with the fetched [Property] if found.
     */
    suspend fun getProperty(
        request: GetPropertyRequest,
    ): Result<Property?>

    /**
     * Retrieves all properties. Returns the [Result] of the operation with a list of [Property].
     */
    suspend fun getProperties(
        request: GetPropertyListsRequest,
    ): Result<List<Property>>

    /**
     * Updates a property with the given [request]. Returns the [Result] of the operation with the updated [Property].
     */
    suspend fun updateProperty(
        request: UpdatePropertyRequest,
    ): Result<Property>

    /**
     * Deletes a property with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteProperty(
        request: DeletePropertyRequest,
    ): Result<Boolean>
}
