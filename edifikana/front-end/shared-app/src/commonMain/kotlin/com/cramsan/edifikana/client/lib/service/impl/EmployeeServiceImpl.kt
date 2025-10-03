package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.service.EmployeeService
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.network.EmployeeNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.runSuspendCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Default implementation for the [EmployeeService].
 */
class EmployeeServiceImpl(
    private val http: HttpClient,
) : EmployeeService {

    @OptIn(NetworkModel::class)
    override suspend fun getEmployeeList(): Result<List<EmployeeModel>> = runSuspendCatching(TAG) {
        val response = http.get(Routes.Employee.PATH).body<List<EmployeeNetworkResponse>>()
        val employeeList = response.map {
            it.toEmployeeModel()
        }
        employeeList
    }

    @OptIn(NetworkModel::class)
    override suspend fun getEmployee(employeePK: EmployeeId): Result<EmployeeModel> = runSuspendCatching(TAG) {
        val response = http.get("${Routes.Employee.PATH}/${employeePK.empId}").body<EmployeeNetworkResponse>()
        val employee = response.toEmployeeModel()
        employee
    }

    @OptIn(NetworkModel::class)
    override suspend fun createEmployee(
        employee: EmployeeModel.CreateEmployeeRequest,
    ): Result<EmployeeModel> = runSuspendCatching(TAG) {
        val response = http.post(Routes.Employee.PATH) {
            contentType(ContentType.Application.Json)
            setBody(employee.toCreateEmployeeNetworkRequest())
        }.body<EmployeeNetworkResponse>()
        val employeeModel = response.toEmployeeModel()
        employeeModel
    }

    @OptIn(NetworkModel::class)
    override suspend fun updateEmployee(
        employee: EmployeeModel.UpdateEmployeeRequest,
    ): Result<EmployeeModel> = runSuspendCatching(TAG) {
        val response = http.put("${Routes.Employee.PATH}/${employee.employeeId.empId}") {
            contentType(ContentType.Application.Json)
            setBody(employee.toUpdateEmployeeNetworkRequest())
        }.body<EmployeeNetworkResponse>()

        val employeeModel = response.toEmployeeModel()
        employeeModel
    }

    companion object {
        private const val TAG = "EmployeeServiceImpl"
    }
}
