package com.cramsan.edifikana.lib.model.employee

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an employee ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of an employee.")
@JsonSchema.Example("\"emp_a1b2c3d4\"")
value class EmployeeId(val empId: String) : PathParam {
    override fun toString(): String = empId
}
