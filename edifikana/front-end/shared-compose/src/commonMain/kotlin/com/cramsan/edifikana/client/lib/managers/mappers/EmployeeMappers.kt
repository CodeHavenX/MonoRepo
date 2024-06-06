package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.FireStoreModel

@FireStoreModel
fun Employee.toDomainModel(): EmployeeModel {
    return EmployeeModel(
        employeePK = documentId(),
        id = id ?: TODO("Employee id cannot be null"),
        idType = idType ?: TODO("Employee id type cannot be null"),
        name = name ?: TODO("Employee name cannot be null"),
        lastName = lastName ?: TODO("Employee last name cannot be null"),
        role = role ?: TODO("Employee role cannot be null"),
    )
}

@FireStoreModel
fun EmployeeModel.toFirebaseModel() = Employee(
    id = id,
    idType = idType,
    name = name,
    lastName = lastName,
    role = role,
)
