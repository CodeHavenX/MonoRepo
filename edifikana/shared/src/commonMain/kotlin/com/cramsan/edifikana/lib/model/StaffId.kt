package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a staff ID.
 */
@Serializable
data class StaffId(val staffId: String) {
    override fun toString(): String = staffId
}
