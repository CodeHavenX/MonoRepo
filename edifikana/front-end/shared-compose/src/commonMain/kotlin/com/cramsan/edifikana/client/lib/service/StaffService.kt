package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.StaffModel

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
    suspend fun getStaff(staffPK: StaffPK): Result<StaffModel>

    /**
     * Create a new staff.
     */
    suspend fun createStaff(staff: StaffModel.CreateStaffRequest): Result<StaffModel>
}
