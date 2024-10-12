package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole

/**
 * Model for a staff.
 * TODO: Add the property ID.
 */
data class StaffModel(
    val id: StaffId,
    val idType: IdType,
    val name: String, // TODO: Rename to first name
    val lastName: String,
    val role: StaffRole,
) {

    /**
     * Request to create a new staff.
     * TODO: Add the property ID.
     */
    data class CreateStaffRequest(
        val idType: IdType,
        val firstName: String,
        val lastName: String,
        val role: StaffRole,
    )
}

/**
 * Returns the full name of the staff.
 */
fun StaffModel.fullName() = "$name $lastName".trim()
