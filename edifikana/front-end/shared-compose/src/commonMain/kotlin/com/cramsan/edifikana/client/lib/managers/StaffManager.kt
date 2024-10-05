package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.client.lib.utils.getOrCatch
import com.cramsan.edifikana.lib.StaffPK
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
    suspend fun getStaffs(): Result<List<StaffModel>> = workContext.getOrCatch(TAG) {
        logI(TAG, "getStaffs")
        staffService.getStaff().getOrThrow()
    }

    /**
     * Get a specific staff.
     */
    suspend fun getStaff(staffPK: StaffPK): Result<StaffModel> = workContext.getOrCatch(TAG) {
        logI(TAG, "getStaff")
        staffService.getStaffs(staffPK).getOrThrow()
    }

    /**
     * Add a staff.
     */
    suspend fun addStaff(staff: StaffModel) = workContext.getOrCatch(TAG) {
        logI(TAG, "addStaff")
        staffService.createStaff(staff).getOrThrow()
    }

    companion object {
        private const val TAG = "StaffManager"
    }
}
