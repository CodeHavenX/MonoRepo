package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.service.EmployeeService
import com.cramsan.edifikana.client.lib.utils.getOrCatch
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.framework.logging.logI

class EmployeeManager(
    private val employeeService: EmployeeService,
    private val workContext: WorkContext,
) {
    suspend fun getEmployees(): Result<List<EmployeeModel>> = workContext.getOrCatch(TAG) {
        logI(TAG, "getEmployees")
        employeeService.getEmployees().getOrThrow()
    }

    suspend fun getEmployee(employeePK: EmployeePK): Result<EmployeeModel> = workContext.getOrCatch(TAG) {
        logI(TAG, "getEmployee")
        employeeService.getEmployee(employeePK).getOrThrow()
    }

    suspend fun addEmployee(employee: EmployeeModel) = workContext.getOrCatch(TAG) {
        logI(TAG, "addEmployee")
        employeeService.addEmployee(employee).getOrThrow()
    }

    companion object {
        private const val TAG = "EmployeeManager"
    }
}
