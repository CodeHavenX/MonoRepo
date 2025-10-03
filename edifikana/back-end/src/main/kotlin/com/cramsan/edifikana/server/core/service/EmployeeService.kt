package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.datastore.EmployeeDatastore
import com.cramsan.edifikana.server.core.service.models.Employee
import com.cramsan.framework.logging.logD

/**
 * Service for employee operations.
 */
class EmployeeService(
    private val employeeDatastore: EmployeeDatastore,
) {

    /**
     * Creates an employee with the provided [name].
     */
    suspend fun createEmployee(
        idType: IdType,
        firstName: String,
        lastName: String,
        role: EmployeeRole,
        propertyId: PropertyId,
    ): Employee {
        logD(TAG, "createEmployee")
        return employeeDatastore.createEmployee(
            idType = idType,
            firstName = firstName,
            lastName = lastName,
            role = role,
            propertyId = propertyId,
        ).getOrThrow()
    }

    /**
     * Retrieves an employee with the provided [id].
     */
    suspend fun getEmployee(
        id: EmployeeId,
    ): Employee? {
        logD(TAG, "getEmployee")
        val employee = employeeDatastore.getEmployee(
            id = id,
        ).getOrNull()

        return employee
    }

    /**
     * Retrieves all employee.
     */
    suspend fun getEmployees(
        clientContext: ClientContext.AuthenticatedClientContext,
    ): List<Employee> {
        logD(TAG, "getEmployees")
        val employees = employeeDatastore.getEmployees(
            currentUser = clientContext.userId,
        ).getOrThrow()
        return employees
    }

    /**
     * Updates an employee with the provided [id] and [name].
     */
    suspend fun updateEmployee(
        id: EmployeeId,
        idType: IdType?,
        firstName: String?,
        lastName: String?,
        role: EmployeeRole?,
    ): Employee {
        logD(TAG, "updateEmployee")
        return employeeDatastore.updateEmployee(
            employeeId = id,
            idType = idType,
            firstName = firstName,
            lastName = lastName,
            role = role,
        ).getOrThrow()
    }

    /**
     * Deletes an employee with the provided [id].
     */
    suspend fun deleteEmployee(
        id: EmployeeId,
    ): Boolean {
        logD(TAG, "deleteEmployee")
        return employeeDatastore.deleteEmployee(
            id = id,
        ).getOrThrow()
    }

    companion object {
        private const val TAG = "EmployeeService"
    }
}
