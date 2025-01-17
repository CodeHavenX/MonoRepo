package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.server.core.repository.StaffDatabase
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.requests.CreateStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateStaffRequest
import com.cramsan.framework.logging.logD

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
        idType: IdType,
        firstName: String,
        lastName: String,
        role: StaffRole,
        propertyId: PropertyId,
    ): Staff {
        logD(TAG, "createStaff")
        return staffDatabase.createStaff(
            request = CreateStaffRequest(
                idType = idType,
                firstName = firstName,
                lastName = lastName,
                role = role,
                propertyId = propertyId,
            ),
        ).getOrThrow()
    }

    /**
     * Retrieves a staff with the provided [id].
     */
    suspend fun getStaff(
        id: StaffId,
    ): Staff? {
        logD(TAG, "getStaff")
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
        logD(TAG, "getStaffs")
        val staffs = staffDatabase.getStaffs().getOrThrow()
        return staffs
    }

    /**
     * Updates a staff with the provided [id] and [name].
     */
    suspend fun updateStaff(
        id: StaffId,
        idType: IdType?,
        firstName: String?,
        lastName: String?,
        role: StaffRole?,
    ): Staff {
        logD(TAG, "updateStaff")
        return staffDatabase.updateStaff(
            request = UpdateStaffRequest(
                id = id,
                idType = idType,
                firstName = firstName,
                lastName = lastName,
                role = role,
            ),
        ).getOrThrow()
    }

    /**
     * Deletes a staff with the provided [id].
     */
    suspend fun deleteStaff(
        id: StaffId,
    ): Boolean {
        logD(TAG, "deleteStaff")
        return staffDatabase.deleteStaff(
            request = DeleteStaffRequest(
                id = id,
            )
        ).getOrThrow()
    }

    companion object {
        private const val TAG = "StaffService"
    }
}
