package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.service.EmployeeService
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager for employee.
 */
class EmployeeManager(private val employeeService: EmployeeService, private val dependencies: ManagerDependencies) {

    /**
     * Get all employees.
     */
    suspend fun getEmployeeList(): Result<List<EmployeeModel>> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getEmployeeList")
        employeeService.getEmployeeList().getOrThrow()
    }

    /**
     * Get a specific employee.
     */
    suspend fun getEmployee(employeePK: EmployeeId): Result<EmployeeModel> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getEmployee")
        employeeService.getEmployee(employeePK).getOrThrow()
    }

    /**
     * Add a employee.
     */
    suspend fun addEmployee(employee: EmployeeModel.CreateEmployeeRequest) = dependencies.getOrCatch(TAG) {
        logI(TAG, "addEmployee")
        employeeService.createEmployee(employee).getOrThrow()
    }

    /**
     * Update a employee.
     */
    suspend fun updateEmployee(employeeModel: EmployeeModel.UpdateEmployeeRequest): Result<EmployeeModel> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "updateEmployee")
            this@EmployeeManager.employeeService.updateEmployee(employeeModel).getOrThrow()
        }

    companion object {
        private const val TAG = "EmployeeManager"
    }
}
