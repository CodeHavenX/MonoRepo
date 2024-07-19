package com.cramsan.edifikana.lib.supa

import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.firestore.IdType
import kotlinx.serialization.Serializable

@Serializable
@SupabaseModel
data class Employee(
    val pk: String,
    val id: String? = null,
    val idType: IdType? = null,
    val name: String? = null,
    val lastName: String? = null,
    val role: EmployeeRole? = null,
) {

    companion object {
        const val COLLECTION = "employees"

        private fun documentId(
            id: String? = null,
            idType: IdType? = null,
        ): EmployeePK {
            requireNotNull(id)
            require(id.isNotBlank())
            requireNotNull(idType)
            return EmployeePK("${idType?.name}_$id")
        }

        fun create(
            id: String,
            idType: IdType,
            name: String,
            lastName: String,
            role: EmployeeRole,
        ): Employee {
            return Employee(
                pk = documentId(id, idType).documentPath,
                id = id,
                idType = idType,
                name = name,
                lastName = lastName,
                role = role,
            )
        }
    }
}
