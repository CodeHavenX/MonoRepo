package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.service.models.Employee
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity for the v_user_employees view.
 * Contains employee data plus the user_id from the property mapping.
 */
@Serializable
@SupabaseModel
data class UserEmployeeViewEntity(
    val id: String,
    @SerialName("id_type")
    val idType: IdType,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val role: EmployeeRole,
    @SerialName("property_id")
    val propertyId: String,
    @SerialName("user_id")
    val userId: String,
) {
    /**
     * Converts this view entity to an Employee domain model.
     */
    fun toEmployee(): Employee = Employee(
        id = EmployeeId(id),
        idType = idType,
        firstName = firstName,
        lastName = lastName,
        role = role,
        propertyId = PropertyId(propertyId),
    )
}
