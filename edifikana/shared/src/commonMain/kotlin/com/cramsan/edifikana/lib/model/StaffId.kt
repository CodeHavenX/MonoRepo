package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a staff ID.
 */
@JvmInline
@Serializable
value class StaffId(val staffId: String) {
    override fun toString(): String = staffId
}
