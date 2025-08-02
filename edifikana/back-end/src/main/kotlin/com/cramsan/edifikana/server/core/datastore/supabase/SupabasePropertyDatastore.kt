package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.server.core.datastore.PropertyDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.models.PropertyEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.UserPropertyMappingEntity
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeletePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyListsRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePropertyRequest
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Datastore for managing properties.
 */
class SupabasePropertyDatastore(
    private val postgrest: Postgrest,
) : PropertyDatastore {

    /**
     * Creates a new property for the given [request]. Returns the [Result] of the operation with the created [Property].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createProperty(
        request: CreatePropertyRequest,
    ): Result<Property> = runSuspendCatching(TAG) {
        logD(TAG, "Creating property: %s", request.name)
        val requestEntity: PropertyEntity.CreatePropertyEntity = request.toPropertyEntity()

        val createdProperty = postgrest.from(PropertyEntity.COLLECTION).insert(requestEntity) {
            select()
        }.decodeSingle<PropertyEntity>()
        logD(TAG, "Property created propertyId: %s", createdProperty.id)
        createdProperty.toProperty()
    }

    /**
     * Retrieves a property for the given [request]. Returns the [Result] of the operation with the fetched [Property] if found.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getProperty(
        request: GetPropertyRequest,
    ): Result<Property?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting property: %s", request.propertyId)

        val propertyEntity = postgrest.from(PropertyEntity.COLLECTION).select {
            filter {
                PropertyEntity::id eq request.propertyId.propertyId
            }
        }.decodeSingleOrNull<PropertyEntity>()

        propertyEntity?.toProperty()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getProperties(
        request: GetPropertyListsRequest,
    ): Result<List<Property>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all properties")
        val userId = request.userId

        val propertyIds =
            postgrest.from(UserPropertyMappingEntity.COLLECTION).select {
                filter { UserPropertyMappingEntity::userId eq userId.userId }
                select()
            }.decodeList<UserPropertyMappingEntity>().map { it.propertyId }

        postgrest.from(PropertyEntity.COLLECTION).select {
            filter { PropertyEntity::id isIn propertyIds }
            select()
        }.decodeList<PropertyEntity>().map { it.toProperty() }
    }

    /**
     * Updates a property with the given [request]. Returns the [Result] of the operation with the updated [Property].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateProperty(
        request: UpdatePropertyRequest,
    ): Result<Property> = runSuspendCatching(TAG) {
        logD(TAG, "Updating property: %s", request.propertyId)

        postgrest.from(PropertyEntity.COLLECTION).update(
            {
                request.name?.let { value -> Property::name setTo value }
            }
        ) {
            select()
            filter {
                PropertyEntity::id eq request.propertyId.propertyId
            }
        }.decodeSingle<PropertyEntity>().toProperty()
    }

    /**
     * Deletes a property with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteProperty(
        request: DeletePropertyRequest,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting property: %s", request.propertyId)

        postgrest.from(PropertyEntity.COLLECTION).delete {
            select()
            filter {
                PropertyEntity::id eq request.propertyId.propertyId
            }
        }.decodeSingleOrNull<PropertyEntity>() != null
    }

    companion object {
        const val TAG = "SupabasePropertyDatastore"
    }
}
