package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.lib.model.StaffId

/**
 * Service for managing staff.
 */
interface StaffService {

    /**
     * Get all staff.
     */
    suspend fun getStaffList(): Result<List<StaffModel>>

    /**
     * Get a specific staff.
     */
    suspend fun getStaff(staffPK: StaffId): Result<StaffModel>

    /**
     * Create a new staff.
     */
    suspend fun createStaff(staff: StaffModel.CreateStaffRequest): Result<StaffModel>
}
