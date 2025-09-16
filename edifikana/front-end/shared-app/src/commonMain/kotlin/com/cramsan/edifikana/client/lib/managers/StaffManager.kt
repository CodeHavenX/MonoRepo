package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager for staff.
 */
class StaffManager(
    private val staffService: StaffService,
    private val dependencies: ManagerDependencies,
) {

    /**
     * Get all staffs.
     */
    suspend fun getStaffList(): Result<List<StaffModel>> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getStaffList")
        staffService.getStaffList().getOrThrow()
    }

    /**
     * Get a specific staff.
     */
    suspend fun getStaff(staffPK: StaffId): Result<StaffModel> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getStaff")
        staffService.getStaff(staffPK).getOrThrow()
    }

    /**
     * Add a staff.
     */
    suspend fun addStaff(staff: StaffModel.CreateStaffRequest) = dependencies.getOrCatch(TAG) {
        logI(TAG, "addStaff")
        staffService.createStaff(staff).getOrThrow()
    }

    /**
     * Update a staff.
     */
    suspend fun updateStaff(staffModel: StaffModel.UpdateStaffRequest): Result<StaffModel> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "updateStaff")
            staffService.updateStaff(staffModel).getOrThrow()
        }

    companion object {
        private const val TAG = "StaffManager"
    }
}
