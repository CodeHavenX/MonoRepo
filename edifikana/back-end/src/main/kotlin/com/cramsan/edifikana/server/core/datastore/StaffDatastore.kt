package com.cramsan.edifikana.server.core.datastore

import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.requests.CreateStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetStaffListRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateStaffRequest

/**
 * Interface for interacting with the staff database.
 */
interface StaffDatastore {

    /**
     * Creates a new staff member for the given [request]. Returns the [Result] of the operation with the created [Staff].
     */
    suspend fun createStaff(
        request: CreateStaffRequest,
    ): Result<Staff>

    /**
     * Retrieves a staff member for the given [request]. Returns the [Result] of the operation with the fetched [Staff] if found.
     */
    suspend fun getStaff(
        request: GetStaffRequest,
    ): Result<Staff?>

    /**
     * Retrieves all staff members. Returns the [Result] of the operation with a list of [Staff].
     */
    suspend fun getStaffs(request: GetStaffListRequest): Result<List<Staff>>

    /**
     * Updates a staff member with the given [request]. Returns the [Result] of the operation with the updated [Staff].
     */
    suspend fun updateStaff(
        request: UpdateStaffRequest,
    ): Result<Staff>

    /**
     * Deletes a staff member with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteStaff(
        request: DeleteStaffRequest,
    ): Result<Boolean>
}
