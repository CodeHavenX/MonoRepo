@file:Suppress("MagicNumber")

package com.cramsan.edifikana.server.core.repository.dummy

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.server.core.repository.StaffDatabase
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.server.core.service.models.requests.CreateStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateStaffRequest
import kotlinx.coroutines.delay

/**
 * Dummy implementation of [StaffDatabase].
 */
class DummyStaffDatabase : StaffDatabase {
    override suspend fun createStaff(request: CreateStaffRequest): Result<Staff> {
        delay(1000)
        return Result.success(
            Staff(
                id = StaffId("1"),
                firstName = "Test",
                lastName = "Test",
                propertyId = PropertyId("1"),
                role = StaffRole.CLEANING,
                idType = IdType.DNI,
            )
        )
    }

    override suspend fun getStaff(request: GetStaffRequest): Result<Staff?> {
        delay(1000)
        return Result.success(
            Staff(
                id = StaffId("1"),
                firstName = "Test",
                lastName = "Test",
                propertyId = PropertyId("1"),
                role = StaffRole.CLEANING,
                idType = IdType.DNI,
            )
        )
    }

    override suspend fun getStaffs(): Result<List<Staff>> {
        delay(1000)
        return Result.success(
            (0..10).map {
                Staff(
                    id = StaffId(it.toString()),
                    firstName = "Test $it",
                    lastName = "$it",
                    propertyId = PropertyId(it.toString()),
                    role = StaffRole.CLEANING,
                    idType = IdType.DNI,
                )
            }
        )
    }

    override suspend fun updateStaff(request: UpdateStaffRequest): Result<Staff> {
        delay(1000)
        return Result.success(
            Staff(
                id = StaffId("1"),
                firstName = "Test",
                lastName = "Test",
                propertyId = PropertyId("1"),
                role = StaffRole.CLEANING,
                idType = IdType.DNI,
            )
        )
    }

    override suspend fun deleteStaff(request: DeleteStaffRequest): Result<Boolean> {
        delay(1000)
        return Result.success(true)
    }
}
