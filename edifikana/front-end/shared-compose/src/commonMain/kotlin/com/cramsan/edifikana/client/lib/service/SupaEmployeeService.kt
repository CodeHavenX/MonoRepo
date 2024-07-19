package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.managers.supamappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.supamappers.toSupabaseModel
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.supa.Employee
import com.cramsan.edifikana.lib.supa.SupabaseModel
import io.github.jan.supabase.postgrest.Postgrest

class SupaEmployeeService(
    private val postgrest: Postgrest,
) : EmployeeService {

    @OptIn(SupabaseModel::class)
    override suspend fun getEmployees(): Result<List<EmployeeModel>> = runSuspendCatching(TAG) {
        postgrest.from(Employee.COLLECTION)
            .select()
            .decodeList<Employee>()
            .map { it.toDomainModel() }
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getEmployee(employeePK: EmployeePK): Result<EmployeeModel> = runSuspendCatching(TAG) {
        postgrest.from(Employee.COLLECTION)
            .select {
                filter {
                    eq("pk", employeePK.documentPath)
                }
                limit(1)
                single()
            }
            .decodeAs<Employee>()
            .toDomainModel()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun addEmployee(employee: EmployeeModel): Result<Unit> = runSuspendCatching(TAG) {
        postgrest.from(Employee.COLLECTION)
            .insert(employee.toSupabaseModel())
    }

    companion object {
        private const val TAG = "SupaEmployeeService"
    }
}
