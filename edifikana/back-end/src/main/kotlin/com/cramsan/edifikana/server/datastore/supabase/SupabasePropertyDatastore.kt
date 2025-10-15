package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.PropertyDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.PropertyEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserPropertyMappingEntity
import com.cramsan.edifikana.server.service.models.Property
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
        name: String,
        address: String,
        creatorUserId: UserId,
        organizationId: OrganizationId,
    ): Result<Property> = runSuspendCatching(TAG) {
        logD(TAG, "Creating property: %s", name)
        val requestEntity: PropertyEntity.CreatePropertyEntity = CreatePropertyEntity(
            name = name,
            address = address,
            organizationId = organizationId,
        )

        // Insert the property into the database and select the created entity
        val createdProperty = postgrest.from(PropertyEntity.COLLECTION).insert(requestEntity) {
            select()
        }.decodeSingle<PropertyEntity>()

        // Now associate this entry with the user that created it
        postgrest.from(UserPropertyMappingEntity.COLLECTION).insert(
            UserPropertyMappingEntity.CreateUserPropertyMappingEntity(
                userId = creatorUserId.userId,
                propertyId = createdProperty.id,
            )
        ) {
            select()
        }.decodeSingleOrNull<UserPropertyMappingEntity>() ?: run {
            throw IllegalStateException("Failed to associate property with user")
        }

        logD(TAG, "Property created propertyId: %s", createdProperty.id)
        createdProperty.toProperty()
    }

    /**
     * Retrieves a property for the given [request]. Returns the [Result] of the operation with the fetched [Property] if found.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getProperty(
        propertyId: PropertyId,
    ): Result<Property?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting property: %s", propertyId)

        val propertyEntity = postgrest.from(PropertyEntity.COLLECTION).select {
            filter {
                PropertyEntity::id eq propertyId.propertyId
            }
        }.decodeSingleOrNull<PropertyEntity>()

        propertyEntity?.toProperty()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getProperties(
        userId: UserId
    ): Result<List<Property>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all properties")

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
        propertyId: PropertyId,
        name: String?,
    ): Result<Property> = runSuspendCatching(TAG) {
        logD(TAG, "Updating property: %s", propertyId)

        postgrest.from(PropertyEntity.COLLECTION).update(
            {
                name?.let { value -> Property::name setTo value }
            }
        ) {
            select()
            filter {
                PropertyEntity::id eq propertyId.propertyId
            }
        }.decodeSingle<PropertyEntity>().toProperty()
    }

    /**
     * Deletes a property with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteProperty(
        propertyId: PropertyId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting property: %s", propertyId)

        // Delete the property mappings first
        postgrest.from(UserPropertyMappingEntity.COLLECTION).delete {
            select()
            filter {
                UserPropertyMappingEntity::propertyId eq propertyId.propertyId
            }
        }

        // Then delete the property itself
        postgrest.from(PropertyEntity.COLLECTION).delete {
            select()
            filter {
                PropertyEntity::id eq propertyId.propertyId
            }
        }.decodeSingleOrNull<PropertyEntity>() != null
    }

    companion object {
        const val TAG = "SupabasePropertyDatastore"
    }
}
