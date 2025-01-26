package com.cramsan.edifikana.lib.model

import kotlin.jvm.JvmInline

/**
 * Domain model representing a staff ID.
 */
@JvmInline
value class StaffId(val staffId: String) {
    override fun toString(): String = staffId
}
