package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.client.lib.utils.getOrCatch
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.logging.logI

/**
 * Manager for staff.
 */
class StaffManager(
    private val staffService: StaffService,
    private val workContext: WorkContext,
) {

    /**
     * Get all staffs.
     */
    suspend fun getStaffList(): Result<List<StaffModel>> = workContext.getOrCatch(TAG) {
        logI(TAG, "getStaffList")
        staffService.getStaffList().getOrThrow()
    }

    /**
     * Get a specific staff.
     */
    suspend fun getStaff(staffPK: StaffId): Result<StaffModel> = workContext.getOrCatch(TAG) {
        logI(TAG, "getStaff")
        staffService.getStaff(staffPK).getOrThrow()
    }

    /**
     * Add a staff.
     */
    suspend fun addStaff(staff: StaffModel.CreateStaffRequest) = workContext.getOrCatch(TAG) {
        logI(TAG, "addStaff")
        staffService.createStaff(staff).getOrThrow()
    }

    companion object {
        private const val TAG = "StaffManager"
    }
}
