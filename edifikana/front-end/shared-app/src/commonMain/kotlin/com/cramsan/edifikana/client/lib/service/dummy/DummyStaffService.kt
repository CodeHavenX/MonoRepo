package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.lib.model.StaffId
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * Dummy implementation for the [StaffService].
 */
class DummyStaffService : StaffService {

    override suspend fun getStaffList(): Result<List<StaffModel>> {
        delay(1.seconds)
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
        delay(1.seconds)
        return Result.success(STAFF_1)
    }

    override suspend fun createStaff(
        staff: StaffModel.CreateStaffRequest,
    ): Result<StaffModel> {
        delay(1.seconds)
        return Result.success(STAFF_1)
    }
}
