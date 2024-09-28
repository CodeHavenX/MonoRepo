package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.server.core.repository.StaffDatabase
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.StaffId
import com.cramsan.edifikana.server.core.service.models.requests.CreateStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateStaffRequest

/**
 * Service for staff operations.
 */
class StaffService(
    private val staffDatabase: StaffDatabase,
) {

    /**
     * Creates a staff with the provided [name].
     */
    suspend fun createStaff(
        name: String,
    ): Staff {
        return staffDatabase.createStaff(
            request = CreateStaffRequest(
                name = name,
            ),
        ).getOrThrow()
    }

    /**
     * Retrieves a staff with the provided [id].
     */
    suspend fun getStaff(
        id: StaffId,
    ): Staff? {
        val staff = staffDatabase.getStaff(
            request = GetStaffRequest(
                id = id,
            ),
        ).getOrNull()

        return staff
    }

    /**
     * Retrieves all staff.
     */
    suspend fun getStaffs(): List<Staff> {
        val staffs = staffDatabase.getStaffs().getOrThrow()
        return staffs
    }

    /**
     * Updates a staff with the provided [id] and [name].
     */
    suspend fun updateStaff(
        id: StaffId,
        name: String?,
    ): Staff {
        return staffDatabase.updateStaff(
            request = UpdateStaffRequest(
                id = id,
                name = name,
            ),
        ).getOrThrow()
    }

    /**
     * Deletes a staff with the provided [id].
     */
    suspend fun deleteStaff(
        id: StaffId,
    ): Boolean {
        return staffDatabase.deleteStaff(
            request = DeleteStaffRequest(
                id = id,
            )
        ).getOrThrow()
    }
}
