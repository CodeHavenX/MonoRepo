package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.datastore.StaffDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.models.StaffEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.UserPropertyMappingEntity
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Datastore for managing staff members.
 */
class SupabaseStaffDatastore(
    private val postgrest: Postgrest,
) : StaffDatastore {

    /**
     * Creates a new staff member for the given [request]. Returns the [Result] of the operation with the created [Staff].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createStaff(
        idType: IdType,
        firstName: String,
        lastName: String,
        role: StaffRole,
        propertyId: PropertyId,
    ): Result<Staff> = runSuspendCatching(TAG) {
        logD(TAG, "Creating staff: %s", firstName)
        val requestEntity: StaffEntity.CreateStaffEntity = CreateStaffEntity(
            idType = idType,
            firstName = firstName,
            lastName = lastName,
            role = role,
            propertyId = propertyId,
        )

        val createdStaff = postgrest.from(StaffEntity.COLLECTION).insert(requestEntity) {
            select()
        }.decodeSingle<StaffEntity>()
        logD(TAG, "Staff created staffId: %s", createdStaff.id)
        createdStaff.toStaff()
    }

    /**
     * Retrieves a staff member for the given [request]. Returns the [Result] of the operation with the fetched [Staff] if found.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getStaff(
        id: StaffId,
    ): Result<Staff?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting staff: %s", id)

        val staffEntity = postgrest.from(StaffEntity.COLLECTION).select {
            filter {
                StaffEntity::id eq id.staffId
            }
        }.decodeSingleOrNull<StaffEntity>()

        staffEntity?.toStaff()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getStaffs(currentUser: UserId): Result<List<Staff>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all staff members")

        val propertyIds =
            postgrest.from(UserPropertyMappingEntity.COLLECTION).select {
                filter { UserPropertyMappingEntity::userId eq currentUser }
                select()
            }.decodeList<UserPropertyMappingEntity>().map { it.propertyId }

        postgrest.from(StaffEntity.COLLECTION).select {
            filter { StaffEntity::propertyId isIn propertyIds }
            select()
        }.decodeList<StaffEntity>().map { it.toStaff() }
    }

    /**
     * Updates a staff member with the given [request]. Returns the [Result] of the operation with the updated [Staff].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateStaff(
        staffId: StaffId,
        idType: IdType?,
        firstName: String?,
        lastName: String?,
        role: StaffRole?,
    ): Result<Staff> = runSuspendCatching(TAG) {
        logD(TAG, "Updating staff: %s", staffId)

        postgrest.from(StaffEntity.COLLECTION).update(
            {
                firstName?.let { value -> Staff::firstName setTo value }
                lastName?.let { value -> Staff::lastName setTo value }
                role?.let { value -> Staff::role setTo value }
                idType?.let { value -> Staff::idType setTo value }
            }
        ) {
            select()
            filter {
                StaffEntity::id eq staffId
            }
        }.decodeSingle<StaffEntity>().toStaff()
    }

    /**
     * Deletes a staff member with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteStaff(
        id: StaffId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting staff: %s", id)

        postgrest.from(StaffEntity.COLLECTION).delete {
            select()
            filter {
                StaffEntity::id eq id
            }
        }.decodeSingleOrNull<StaffEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseStaffDatastore"
    }
}
