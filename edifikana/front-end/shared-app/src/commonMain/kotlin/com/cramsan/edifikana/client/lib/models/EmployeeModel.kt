package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Model for a employee.
 * TODO: Add the property ID.
 */
data class EmployeeModel(
    val id: EmployeeId,
    val idType: IdType,
    val firstName: String,
    val lastName: String,
    val role: EmployeeRole,
    val email: String?,
) {

    /**
     * Request to create a new employee.
     */
    data class CreateEmployeeRequest(
        val idType: IdType,
        val firstName: String,
        val lastName: String,
        val role: EmployeeRole,
        val propertyId: PropertyId,
    )

    /**
     * Request to update a employee. Nullable fields are optional, if null they will not be updated.
     */
    data class UpdateEmployeeRequest(
        val employeeId: EmployeeId,
        val firstName: String?,
        val lastName: String?,
        val role: EmployeeRole?,
    )
}

/**
 * Returns the full name of the employee.
 */
fun EmployeeModel.fullName() = "$firstName $lastName".trim()
