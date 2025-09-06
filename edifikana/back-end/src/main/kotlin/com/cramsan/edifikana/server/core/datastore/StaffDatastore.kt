package com.cramsan.edifikana.server.core.datastore

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.Staff

/**
 * Interface for interacting with the staff database.
 */
interface StaffDatastore {

    /**
     * Creates a new staff member with the given details. Returns the [Result] of the operation with the created [Staff].
     */
    suspend fun createStaff(
        idType: IdType,
        firstName: String,
        lastName: String,
        role: StaffRole,
        propertyId: PropertyId,
    ): Result<Staff>

    /**
     * Retrieves a staff member by their ID. Returns the [Result] of the operation with the fetched [Staff] if found.
     */
    suspend fun getStaff(
        id: StaffId,
    ): Result<Staff?>

    /**
     * Retrieves all staff members for the current user. Returns the [Result] of the operation with a list of [Staff].
     */
    suspend fun getStaffs(
        currentUser: UserId,
    ): Result<List<Staff>>

    /**
     * Updates a staff member with the given details. Returns the [Result] of the operation with the updated [Staff].
     */
    suspend fun updateStaff(
        staffId: StaffId,
        idType: IdType?,
        firstName: String?,
        lastName: String?,
        role: StaffRole?,
    ): Result<Staff>

    /**
     * Deletes a staff member by their ID. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteStaff(
        id: StaffId,
    ): Result<Boolean>
}
