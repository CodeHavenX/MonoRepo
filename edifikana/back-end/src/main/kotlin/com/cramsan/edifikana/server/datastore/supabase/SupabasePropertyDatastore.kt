package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.PropertyDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.PropertyEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserPropertyMappingEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserPropertyViewEntity
import com.cramsan.edifikana.server.service.models.Property
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Clock

/**
 * Datastore for managing properties.
 */
class SupabasePropertyDatastore(
    private val postgrest: Postgrest,
    private val clock: Clock,
) : PropertyDatastore {

    /**
     * Creates a new property and associates it with [creatorUserId].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createProperty(
        name: String,
        address: String,
        creatorUserId: UserId,
        organizationId: OrganizationId,
        imageUrl: String?,
    ): Result<Property> = runSuspendCatching(TAG) {
        logD(TAG, "Creating property: %s", name)
        val requestEntity: PropertyEntity.CreatePropertyEntity = CreatePropertyEntity(
            name = name,
            address = address,
            organizationId = organizationId,
            imageUrl = imageUrl,
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
     * Retrieves a property by [propertyId]. Returns the [Property] if found, null otherwise.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getProperty(
        propertyId: PropertyId,
    ): Result<Property?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting property: %s", propertyId)

        val propertyEntity = postgrest.from(PropertyEntity.COLLECTION).select {
            filter {
                PropertyEntity::id eq propertyId.propertyId
                PropertyEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<PropertyEntity>()

        propertyEntity?.toProperty()
    }

    /**
     * Gets all properties accessible to the given user.
     * Uses the v_user_properties view for single-query retrieval (eliminates N+1 pattern).
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getProperties(
        userId: UserId
    ): Result<List<Property>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all properties for user: %s", userId)

        // Use the v_user_properties view for single-query retrieval
        postgrest.from(VIEW_USER_PROPERTIES).select {
            filter { UserPropertyViewEntity::userId eq userId.userId }
        }.decodeList<UserPropertyViewEntity>().map { it.toProperty() }
    }

    /**
     * Updates a property's attributes. Only non-null parameters are updated.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateProperty(
        propertyId: PropertyId,
        name: String?,
        address: String?,
        imageUrl: String?,
    ): Result<Property> = runSuspendCatching(TAG) {
        logD(TAG, "Updating property: %s", propertyId)

        postgrest.from(PropertyEntity.COLLECTION).update(
            {
                name?.let { value -> PropertyEntity::name setTo value }
                address?.let { value -> PropertyEntity::address setTo value }
                imageUrl?.let { value -> PropertyEntity::imageUrl setTo value }
            }
        ) {
            select()
            filter {
                PropertyEntity::id eq propertyId.propertyId
            }
        }.decodeSingle<PropertyEntity>().toProperty()
    }

    /**
     * Soft deletes a property by [propertyId]. Returns true if successful.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteProperty(
        propertyId: PropertyId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting property: %s", propertyId)

        // Soft delete the property by setting deleted_at timestamp
        postgrest.from(PropertyEntity.COLLECTION).update({
            PropertyEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                PropertyEntity::id eq propertyId.propertyId
                PropertyEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<PropertyEntity>() != null
    }

    /**
     * Permanently deletes a soft-deleted property by [propertyId]. Returns true if successful.
     * Only purges records that are already soft-deleted (deletedAt is not null).
     */
    @OptIn(SupabaseModel::class)
    override suspend fun purgeProperty(
        propertyId: PropertyId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Purging soft-deleted property: %s", propertyId)

        // First verify the record exists and is soft-deleted
        val entity = postgrest.from(PropertyEntity.COLLECTION).select {
            filter {
                PropertyEntity::id eq propertyId.propertyId
            }
        }.decodeSingleOrNull<PropertyEntity>()

        // Only purge if it exists and is soft-deleted
        if (entity?.deletedAt == null) {
            return@runSuspendCatching false
        }

        // Delete the record
        postgrest.from(PropertyEntity.COLLECTION).delete {
            filter {
                PropertyEntity::id eq propertyId.propertyId
            }
        }
        true
    }

    companion object {
        const val TAG = "SupabasePropertyDatastore"
        const val VIEW_USER_PROPERTIES = "v_user_properties"
    }
}
