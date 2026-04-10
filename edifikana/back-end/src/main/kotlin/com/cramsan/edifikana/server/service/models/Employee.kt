package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.employee.EmployeeRole
import com.cramsan.edifikana.lib.model.identification.IdType
import com.cramsan.edifikana.lib.model.property.PropertyId

/**
 * Domain model representing an employee member.
 */
data class Employee(
    val id: EmployeeId,
    val idType: IdType,
    val firstName: String,
    val lastName: String,
    val role: EmployeeRole,
    val propertyId: PropertyId,
)
