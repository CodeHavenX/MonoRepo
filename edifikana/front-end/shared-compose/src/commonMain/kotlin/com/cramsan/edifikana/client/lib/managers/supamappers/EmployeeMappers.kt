package com.cramsan.edifikana.client.lib.managers.supamappers

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.supa.Employee
import com.cramsan.edifikana.lib.supa.SupabaseModel

@SupabaseModel
fun Employee.toDomainModel(): EmployeeModel {
    return EmployeeModel(
        employeePK = EmployeePK(pk),
        id = id ?: TODO("Employee id cannot be null"),
        idType = idType ?: TODO("Employee id type cannot be null"),
        name = name ?: TODO("Employee name cannot be null"),
        lastName = lastName ?: TODO("Employee last name cannot be null"),
        role = role ?: TODO("Employee role cannot be null"),
    )
}

@SupabaseModel
fun EmployeeModel.toSupabaseModel() = Employee.create(
    id = id,
    idType = idType,
    name = name,
    lastName = lastName,
    role = role,
)
