@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import kotlinx.coroutines.delay

/**
 * Dummy implementation of [StaffService] for testing purposes.
 */
class DummyStaffService : StaffService {
    override suspend fun getStaffList(): Result<List<StaffModel>> {
        delay(100)
        return Result.success(
            (0..10).map {
                StaffModel(
                    StaffId(it.toString()),
                    IdType.DNI,
                    "John Doe",
                    "$it",
                    StaffRole.SECURITY,
                )
            }
        )
    }

    override suspend fun getStaff(staffPK: StaffId): Result<StaffModel> {
        return Result.success(
            StaffModel(
                StaffId("1"),
                IdType.DNI,
                "John",
                "Doe",
                StaffRole.SECURITY,
            )
        )
    }

    override suspend fun createStaff(staff: StaffModel.CreateStaffRequest): Result<StaffModel> {
        return Result.success(
            StaffModel(
                StaffId("1"),
                IdType.DNI,
                "John",
                "Doe",
                StaffRole.SECURITY,
            )
        )
    }
}
