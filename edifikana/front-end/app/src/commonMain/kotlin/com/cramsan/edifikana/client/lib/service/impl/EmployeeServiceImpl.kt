package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.api.EmployeeApi
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.service.EmployeeService
import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import io.ktor.client.HttpClient

/**
 * Default implementation for the [EmployeeService].
 */
@FrontendService
class EmployeeServiceImpl(private val http: HttpClient) : EmployeeService {
    override suspend fun getEmployeeList(): Result<List<EmployeeModel>> =
        runSuspendCatching(TAG) {
            val response = EmployeeApi.getEmployees.buildRequest().execute(http)
            val employeeList =
                response.content.map {
                    it.toEmployeeModel()
                }
            employeeList
        }

    override suspend fun getEmployee(employeePK: EmployeeId): Result<EmployeeModel> =
        runSuspendCatching(TAG) {
            val response = EmployeeApi.getEmployee.buildRequest(employeePK).execute(http)
            val employee = response.toEmployeeModel()
            employee
        }

    override suspend fun createEmployee(
        employee: EmployeeModel.CreateEmployeeRequest,
    ): Result<EmployeeModel> =
        runSuspendCatching(TAG) {
            val response =
                EmployeeApi
                    .createEmployee
                    .buildRequest(employee.toCreateEmployeeNetworkRequest())
                    .execute(http)
            val employeeModel = response.toEmployeeModel()
            employeeModel
        }

    override suspend fun updateEmployee(
        employee: EmployeeModel.UpdateEmployeeRequest,
    ): Result<EmployeeModel> =
        runSuspendCatching(TAG) {
            val response =
                EmployeeApi
                    .updateEmployee
                    .buildRequest(employee.employeeId, employee.toUpdateEmployeeNetworkRequest())
                    .execute(http)

            val employeeModel = response.toEmployeeModel()
            employeeModel
        }

    companion object {
        private const val TAG = "EmployeeServiceImpl"
    }
}
