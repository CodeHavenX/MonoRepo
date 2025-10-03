package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.lib.model.EmployeeId

/**
 * Service for managing employee.
 */
interface EmployeeService {

    /**
     * Get all employee.
     */
    suspend fun getEmployeeList(): Result<List<EmployeeModel>>

    /**
     * Get a specific employee.
     */
    suspend fun getEmployee(employeePK: EmployeeId): Result<EmployeeModel>

    /**
     * Create a new employee.
     */
    suspend fun createEmployee(employee: EmployeeModel.CreateEmployeeRequest): Result<EmployeeModel>

    /**
     * Update an existing employee member.
     */
    suspend fun updateEmployee(employee: EmployeeModel.UpdateEmployeeRequest): Result<EmployeeModel>
}
