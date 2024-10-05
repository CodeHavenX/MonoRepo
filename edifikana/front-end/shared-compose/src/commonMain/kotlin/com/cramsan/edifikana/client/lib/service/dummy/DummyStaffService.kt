@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.lib.StaffPK
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import kotlinx.coroutines.delay

/**
 * Dummy implementation of [StaffService] for testing purposes.
 */
class DummyStaffService : StaffService {
    override suspend fun getStaff(): Result<List<StaffModel>> {
        delay(100)
        return Result.success(
            (0..10).map {
                StaffModel(
                    StaffPK(it.toString()),
                    "John Doe",
                    IdType.DNI,
                    "$it",
                    "$it",
                    StaffRole.SECURITY,
                )
            }
        )
    }

    override suspend fun getStaffs(staffPK: StaffPK): Result<StaffModel> {
        return Result.success(
            StaffModel(
                StaffPK("1"),
                "23",
                IdType.DNI,
                "John",
                "Doe",
                StaffRole.SECURITY,
            )
        )
    }

    override suspend fun createStaff(staff: StaffModel): Result<Unit> {
        return Result.success(Unit)
    }
}
