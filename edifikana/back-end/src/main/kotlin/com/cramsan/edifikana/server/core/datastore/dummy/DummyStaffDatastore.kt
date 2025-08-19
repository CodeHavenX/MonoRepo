package com.cramsan.edifikana.server.core.datastore.dummy

import com.cramsan.edifikana.server.core.datastore.StaffDatastore
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.requests.CreateStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetStaffListRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateStaffRequest
import com.cramsan.framework.logging.logD

/**
 * Class with dummy data to be used only for development and testing.
 */
class DummyStaffDatastore : StaffDatastore {
    override suspend fun createStaff(request: CreateStaffRequest): Result<Staff> {
        logD(TAG, "createStaff")
        return Result.success(STAFF_1)
    }

    override suspend fun getStaff(request: GetStaffRequest): Result<Staff?> {
        logD(TAG, "getStaff")
        return Result.success(STAFF_1)
    }

    override suspend fun getStaffs(request: GetStaffListRequest): Result<List<Staff>> {
        logD(TAG, "getStaffs")
        return Result.success(listOf(STAFF_1, STAFF_2, STAFF_3, STAFF_4))
    }

    override suspend fun updateStaff(request: UpdateStaffRequest): Result<Staff> {
        logD(TAG, "updateStaff")
        return Result.success(STAFF_1)
    }

    override suspend fun deleteStaff(request: DeleteStaffRequest): Result<Boolean> {
        logD(TAG, "deleteStaff")
        return Result.success(true)
    }

    companion object {
        private const val TAG = "DummyStaffDatastore"
    }
}
