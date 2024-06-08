package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.lib.firestore.EmployeePK

interface EmployeeService {

    suspend fun getEmployees(): Result<List<EmployeeModel>>

    suspend fun getEmployee(employeePK: EmployeePK): Result<EmployeeModel>

    suspend fun addEmployee(employee: EmployeeModel): Result<Unit>
}
