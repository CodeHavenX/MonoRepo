package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an employee ID.
 */
@JvmInline
@Serializable
value class EmployeeId(val empId: String) {
    override fun toString(): String = empId
}
