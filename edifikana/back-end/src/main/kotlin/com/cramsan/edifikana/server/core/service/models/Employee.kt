package com.cramsan.edifikana.server.core.service.models

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId

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
