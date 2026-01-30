package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.service.models.Employee

/**
 * Interface for interacting with the employee database.
 */
interface EmployeeDatastore {

    /**
     * Creates a new employee member with the given details. Returns the [Result] of the operation with the created [Employee].
     */
    suspend fun createEmployee(
        idType: IdType,
        firstName: String,
        lastName: String,
        role: EmployeeRole,
        propertyId: PropertyId,
    ): Result<Employee>

    /**
     * Retrieves an employee member by their ID. Returns the [Result] of the operation with the fetched [Employee] if found.
     */
    suspend fun getEmployee(
        id: EmployeeId,
    ): Result<Employee?>

    /**
     * Retrieves all employee members for the current user. Returns the [Result] of the operation with a list of [Employee].
     */
    suspend fun getEmployees(
        currentUser: UserId,
    ): Result<List<Employee>>

    /**
     * Updates an employee member with the given details. Returns the [Result] of the operation with the updated [Employee].
     */
    suspend fun updateEmployee(
        employeeId: EmployeeId,
        idType: IdType?,
        firstName: String?,
        lastName: String?,
        role: EmployeeRole?,
    ): Result<Employee>

    /**
     * Deletes an employee member by their ID. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteEmployee(
        id: EmployeeId,
    ): Result<Boolean>

    /**
     * Permanently deletes a soft-deleted employee record by ID.
     * Only purges if the record is already soft-deleted.
     * This is intended for testing and maintenance purposes only.
     * Returns the [Result] of the operation with a [Boolean] indicating if the record was purged.
     */
    suspend fun purgeEmployee(
        id: EmployeeId,
    ): Result<Boolean>
}
