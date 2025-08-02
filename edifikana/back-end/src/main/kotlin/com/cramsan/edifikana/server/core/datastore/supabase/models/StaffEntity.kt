package com.cramsan.edifikana.server.core.datastore.supabase.models

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.framework.ammotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity representing a staff member.
 */
@Serializable
@SupabaseModel
data class StaffEntity(
    val id: String,
    @SerialName("id_type")
    val idType: IdType,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val role: StaffRole,
    @SerialName("property_id")
    val propertyId: String,
) {
    companion object {
        const val COLLECTION = "staff"
    }

    /**
     * Entity representing a new staff member.
     */
    @Serializable
    @SupabaseModel
    data class CreateStaffEntity(
        @SerialName("id_type")
        val idType: IdType,
        @SerialName("first_name")
        val firstName: String,
        @SerialName("last_name")
        val lastName: String,
        val role: StaffRole,
        @SerialName("property_id")
        val propertyId: String,
    )
}
