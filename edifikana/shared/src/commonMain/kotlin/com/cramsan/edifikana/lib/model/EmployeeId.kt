package com.cramsan.edifikana.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an employee ID.
 */
@JvmInline
@Serializable
value class EmployeeId(val empId: String) : PathParam {
    override fun toString(): String = empId
}
