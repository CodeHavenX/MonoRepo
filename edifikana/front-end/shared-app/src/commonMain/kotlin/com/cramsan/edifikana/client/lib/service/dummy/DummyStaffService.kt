package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.lib.model.StaffId
import io.ktor.client.request.get

/**
 * Dummy implementation for the [StaffService].
 */
class DummyStaffService : StaffService {

    override suspend fun getStaffList(): Result<List<StaffModel>> {
        return Result.success(
            listOf(
                STAFF_1,
                STAFF_2,
                STAFF_3,
                STAFF_4,
            )
        )
    }

    override suspend fun getStaff(staffPK: StaffId): Result<StaffModel> {
        return Result.success(STAFF_1)
    }

    override suspend fun createStaff(
        staff: StaffModel.CreateStaffRequest,
    ): Result<StaffModel> {
        return Result.success(STAFF_1)
    }
}
