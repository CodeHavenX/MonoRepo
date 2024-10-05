package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.lib.StaffPK

/**
 * Service for managing staff.
 */
interface StaffService {

    /**
     * Get all staff.
     */
    suspend fun getStaff(): Result<List<StaffModel>>

    /**
     * Get a specific staff.
     */
    suspend fun getStaffs(staffPK: StaffPK): Result<StaffModel>

    /**
     * Create a new staff.
     */
    suspend fun createStaff(staff: StaffModel): Result<Unit>
}
