@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.service.EmployeeService
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import kotlinx.coroutines.delay

/**
 * Dummy implementation of [EmployeeService] for testing purposes.
 */
class DummyEmployeeService : EmployeeService {
    override suspend fun getEmployees(): Result<List<EmployeeModel>> {
        delay(100)
        return Result.success(
            (0..10).map {
                EmployeeModel(
                    EmployeePK(it.toString()),
                    "John Doe",
                    IdType.DNI,
                    "$it",
                    "$it",
                    EmployeeRole.SECURITY,
                )
            }
        )
    }

    override suspend fun getEmployee(employeePK: EmployeePK): Result<EmployeeModel> {
        return Result.success(
            EmployeeModel(
                EmployeePK("1"),
                "23",
                IdType.DNI,
                "John",
                "Doe",
                EmployeeRole.SECURITY,
            )
        )
    }

    override suspend fun addEmployee(employee: EmployeeModel): Result<Unit> {
        return Result.success(Unit)
    }
}
