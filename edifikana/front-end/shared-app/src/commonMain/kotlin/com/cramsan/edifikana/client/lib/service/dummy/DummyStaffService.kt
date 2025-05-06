package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.StaffStatus
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * Dummy implementation for the [StaffService].
 */
class DummyStaffService : StaffService {

    private val staffList = mutableListOf(
        STAFF_1,
        STAFF_2,
        STAFF_3,
        STAFF_4,
    )

    override suspend fun getStaffList(): Result<List<StaffModel>> {
        delay(1.seconds)
        return Result.success(staffList)
    }

    override suspend fun getStaff(staffPK: StaffId): Result<StaffModel> {
        delay(1.seconds)
        return Result.success(staffList.firstOrNull { it.id == staffPK } ?: TODO("Staff not found"))
    }

    override suspend fun createStaff(
        staff: StaffModel.CreateStaffRequest,
    ): Result<StaffModel> {
        delay(1.seconds)
        val newStaff = StaffModel(
            id = StaffId(staffList.size.toString()),
            name = staff.firstName,
            lastName = staff.lastName,
            role = staff.role,
            idType = staff.idType,
            email = null,
            status = StaffStatus.ACTIVE,
        )
        staffList.add(newStaff)
        return Result.success(newStaff)
    }

    override suspend fun inviteStaff(email: String): Result<Unit> {
        delay(1.seconds)
        val existingStaff = staffList.firstOrNull { it.email == email }
        if (existingStaff != null) {
            return Result.failure(Exception("Staff with email $email already exists"))
        }
        val newStaff = StaffModel(
            id = StaffId(staffList.size.toString()),
            name = "Dummy",
            lastName = "Staff",
            role = StaffRole.ADMIN,
            idType = IdType.DNI,
            email = email,
            status = StaffStatus.PENDING,
        )
        staffList.add(newStaff)
        return Result.success(Unit)
    }
}
