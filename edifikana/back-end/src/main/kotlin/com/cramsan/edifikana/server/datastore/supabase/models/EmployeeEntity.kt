package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity representing employee member.
 */
@Serializable
@SupabaseModel
data class EmployeeEntity(
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
) {
    companion object {
        const val COLLECTION = "employee"
    }

    /**
     * Entity representing a new employee member.
     */
    @Serializable
    @SupabaseModel
    data class CreateEmployeeEntity(
        @SerialName("id_type")
        val idType: IdType,
        @SerialName("first_name")
        val firstName: String,
        @SerialName("last_name")
        val lastName: String,
        val role: EmployeeRole,
        @SerialName("property_id")
        val propertyId: String,
    )
}
