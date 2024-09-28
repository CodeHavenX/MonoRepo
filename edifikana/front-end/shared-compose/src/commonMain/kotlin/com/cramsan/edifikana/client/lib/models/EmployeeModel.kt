package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType

data class EmployeeModel(
    val employeePK: EmployeePK?,
    val id: String,
    val idType: IdType,
    val name: String,
    val lastName: String,
    val role: EmployeeRole,
)

fun EmployeeModel.fullName() = "$name $lastName".trim()
