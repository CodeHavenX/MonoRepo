package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.server.core.repository.StaffDatabase
import com.cramsan.edifikana.server.core.repository.supabase.models.StaffEntity
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.requests.CreateStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateStaffRequest
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Database for managing staff members.
 */
class SupabaseStaffDatabase(
    private val postgrest: Postgrest,
) : StaffDatabase {

    /**
     * Creates a new staff member for the given [request]. Returns the [Result] of the operation with the created [Staff].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createStaff(
        request: CreateStaffRequest,
    ): Result<Staff> = runSuspendCatching(TAG) {
        logD(TAG, "Creating staff: %s", request.firstName)
        val requestEntity: StaffEntity.CreateStaffEntity = request.toStaffEntity()

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
        request: GetStaffRequest,
    ): Result<Staff?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting staff: %s", request.id)

        val staffEntity = postgrest.from(StaffEntity.COLLECTION).select {
            filter {
                StaffEntity::id eq request.id.staffId
            }
        }.decodeSingleOrNull<StaffEntity>()

        staffEntity?.toStaff()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getStaffs(): Result<List<Staff>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all staff members")

        postgrest.from(StaffEntity.COLLECTION).select {
            select()
        }.decodeList<StaffEntity>().map { it.toStaff() }
    }

    /**
     * Updates a staff member with the given [request]. Returns the [Result] of the operation with the updated [Staff].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateStaff(
        request: UpdateStaffRequest,
    ): Result<Staff> = runSuspendCatching(TAG) {
        logD(TAG, "Updating staff: %s", request.id)

        postgrest.from(StaffEntity.COLLECTION).update(
            {
                request.firstName?.let { value -> Staff::firstName setTo value }
                request.lastName?.let { value -> Staff::lastName setTo value }
                request.role?.let { value -> Staff::role setTo value }
                request.idType?.let { value -> Staff::idType setTo value }
            }
        ) {
            select()
            filter {
                StaffEntity::id eq request.id
            }
        }.decodeSingle<StaffEntity>().toStaff()
    }

    /**
     * Deletes a staff member with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteStaff(
        request: DeleteStaffRequest,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting staff: %s", request.id)

        postgrest.from(StaffEntity.COLLECTION).delete {
            select()
            filter {
                StaffEntity::id eq request.id
            }
        }.decodeSingleOrNull<StaffEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseStaffDatabase"
    }
}
