package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.StaffPK
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole

/**
 * Model for a staff.
 */
data class StaffModel(
    val staffPK: StaffPK?,
    val id: String,
    val idType: IdType,
    val name: String,
    val lastName: String,
    val role: StaffRole,
)

/**
 * Returns the full name of the staff.
 */
fun StaffModel.fullName() = "$name $lastName".trim()
