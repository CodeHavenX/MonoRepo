package com.cramsan.edifikana.client.android.models

import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.firestore.IdType

data class EmployeeModel(
    val employeePK: EmployeePK,
    val id: String,
    val idType: IdType,
    val name: String,
    val lastName: String,
    val role: EmployeeRole,
)

fun EmployeeModel.fullName() = "$name $lastName".trim()
